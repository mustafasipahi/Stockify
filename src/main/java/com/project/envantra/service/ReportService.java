package com.project.envantra.service;

import com.project.envantra.model.dto.*;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.entity.SalesEntity;
import com.project.envantra.model.request.DailyReportRequest;
import com.project.envantra.model.response.DailyReportResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.project.envantra.util.DateUtil.*;
import static com.project.envantra.util.NameUtil.getBrokerFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final BrokerGetService brokerGetService;
    private final SalesService salesService;
    private final PaymentService paymentService;
    private final BrokerVisitGetService brokerVisitGetService;

    public DailyReportResponse getDailyReport(DailyReportRequest request) {
        Map<Long, BrokerDto> brokerMap = getBrokerMap(request.getBrokerId());
        if (brokerMap.isEmpty()) {
            return createEmptyResponse();
        }
        LocalDateTime startDate = getStartDate(request.getStartDate());
        LocalDateTime endDate = getEndDate(request.getEndDate());
        List<SalesEntity> salesList = salesService.findAllByBrokerIdByDate(brokerMap.keySet(), startDate, endDate);
        List<PaymentEntity> paymentList = paymentService.findAllByBrokerIdByDate(brokerMap.keySet(), startDate, endDate);
        List<BrokerVisitDto> visitInfoList = brokerVisitGetService.getVisitInfoListByDate(startDate, endDate);
        return DailyReportResponse.builder()
                .dailyBrokerReports(createBrokerReports(salesList, paymentList, visitInfoList, brokerMap))
                .dailySummaryReports(createSummaryReports(salesList, paymentList))
                .totals(createTotals(salesList, paymentList))
                .build();
    }

    private Map<Long, BrokerDto> getBrokerMap(Long brokerId) {
        return brokerGetService.getBrokerMap(brokerId != null ? List.of(brokerId) : Collections.emptyList());
    }

    private DailyReportResponse createEmptyResponse() {
        return DailyReportResponse.builder()
                .dailyBrokerReports(Collections.emptyList())
                .dailySummaryReports(Collections.emptyList())
                .totals(DailyTotalDto.builder()
                        .totalSalesAmount(BigDecimal.ZERO)
                        .totalPaymentAmount(BigDecimal.ZERO)
                        .profitOrLoss(BigDecimal.ZERO)
                        .build())
                .build();
    }

    private List<DailyBrokerReportDto> createBrokerReports(List<SalesEntity> salesList,
                                                           List<PaymentEntity> paymentList,
                                                           List<BrokerVisitDto> visitInfoList,
                                                           Map<Long, BrokerDto> brokerMap) {
        Map<Long, DailyBrokerReportDto> reportMap = new HashMap<>();
        Map<String, DailyBrokerReportDetailDto> detailMap = new HashMap<>();
        Map<String, BrokerVisitDto> visitInfoMap = createVisitInfoMap(visitInfoList);

        initializeReportsFromSales(salesList, brokerMap, reportMap, detailMap, visitInfoMap);
        initializeReportsFromPayments(paymentList, brokerMap, reportMap, detailMap, visitInfoMap);
        initializeReportsFromVisits(visitInfoList, brokerMap, reportMap, detailMap);

        processSalesAmounts(salesList, detailMap);
        processPaymentAmounts(paymentList, detailMap);

        calculateTotalsAndSort(reportMap);

        return sortReportsByBrokerName(reportMap);
    }

    private Map<String, BrokerVisitDto> createVisitInfoMap(List<BrokerVisitDto> visitInfoList) {
        return visitInfoList.stream()
                .collect(Collectors.toMap(
                        visit -> visit.getBrokerId() + "_" + visit.getVisitDate(),
                        visit -> visit,
                        (existing, replacement) -> existing
                ));
    }

    private void initializeReportsFromSales(List<SalesEntity> salesList,
                                            Map<Long, BrokerDto> brokerMap,
                                            Map<Long, DailyBrokerReportDto> reportMap,
                                            Map<String, DailyBrokerReportDetailDto> detailMap,
                                            Map<String, BrokerVisitDto> visitInfoMap) {
        for (SalesEntity sales : salesList) {
            Long brokerId = sales.getBrokerId();
            if (!brokerMap.containsKey(brokerId)) {
                continue;
            }
            Long date = getTime(sales.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            getOrCreateReport(brokerId, brokerMap, reportMap);
            getOrCreateDetail(detailKey, brokerId, date, visitInfoMap, reportMap, detailMap);
        }
    }

    private void initializeReportsFromPayments(List<PaymentEntity> paymentList,
                                               Map<Long, BrokerDto> brokerMap,
                                               Map<Long, DailyBrokerReportDto> reportMap,
                                               Map<String, DailyBrokerReportDetailDto> detailMap,
                                               Map<String, BrokerVisitDto> visitInfoMap) {
        for (PaymentEntity payment : paymentList) {
            Long brokerId = payment.getBrokerId();
            if (!brokerMap.containsKey(brokerId)) {
                continue;
            }
            Long date = getTime(payment.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            getOrCreateReport(brokerId, brokerMap, reportMap);
            getOrCreateDetail(detailKey, brokerId, date, visitInfoMap, reportMap, detailMap);
        }
    }

    private void initializeReportsFromVisits(List<BrokerVisitDto> visitInfoList,
                                             Map<Long, BrokerDto> brokerMap,
                                             Map<Long, DailyBrokerReportDto> reportMap,
                                             Map<String, DailyBrokerReportDetailDto> detailMap) {
        for (BrokerVisitDto visitInfo : visitInfoList) {
            Long brokerId = visitInfo.getBrokerId();
            if (!brokerMap.containsKey(brokerId)) {
                continue;
            }
            Long date = visitInfo.getVisitDate();
            String detailKey = brokerId + "_" + date;
            if (!detailMap.containsKey(detailKey)) {
                getOrCreateReport(brokerId, brokerMap, reportMap);

                DailyBrokerReportDto report = reportMap.get(brokerId);
                DailyBrokerReportDetailDto detail = createNewDetail(date, visitInfo);
                report.getDailyDetails().add(detail);
                detailMap.put(detailKey, detail);
            }
        }
    }

    private void getOrCreateReport(Long brokerId,
                                   Map<Long, BrokerDto> brokerMap,
                                   Map<Long, DailyBrokerReportDto> reportMap) {
        if (!reportMap.containsKey(brokerId)) {
            BrokerDto broker = brokerMap.get(brokerId);
            DailyBrokerReportDto report = createNewReport(broker);
            reportMap.put(brokerId, report);
        }
    }

    private DailyBrokerReportDto createNewReport(BrokerDto broker) {
        DailyBrokerReportDto report = new DailyBrokerReportDto();
        report.setOrderNo(broker.getOrderNo());
        report.setBrokerFullName(getBrokerFullName(broker));
        report.setDailyDetails(new ArrayList<>());
        report.setTotalSalesAmount(BigDecimal.ZERO);
        report.setTotalPaymentAmount(BigDecimal.ZERO);
        return report;
    }

    private void getOrCreateDetail(String detailKey,
                                   Long brokerId,
                                   Long date,
                                   Map<String, BrokerVisitDto> visitInfoMap,
                                   Map<Long, DailyBrokerReportDto> reportMap,
                                   Map<String, DailyBrokerReportDetailDto> detailMap) {
        if (!detailMap.containsKey(detailKey)) {
            DailyBrokerReportDto report = reportMap.get(brokerId);
            DailyBrokerReportDetailDto detail = createNewDetail(date, visitInfoMap.get(detailKey));
            report.getDailyDetails().add(detail);
            detailMap.put(detailKey, detail);
        }
    }

    private DailyBrokerReportDetailDto createNewDetail(Long date, BrokerVisitDto visitInfo) {
        DailyBrokerReportDetailDto detail = new DailyBrokerReportDetailDto();
        detail.setDate(date);
        detail.setSalesAmount(BigDecimal.ZERO);
        detail.setPaymentAmount(BigDecimal.ZERO);
        detail.setVisitInfo(visitInfo);
        return detail;
    }

    private void processSalesAmounts(List<SalesEntity> salesList,
                                     Map<String, DailyBrokerReportDetailDto> detailMap) {
        for (SalesEntity sales : salesList) {
            Long brokerId = sales.getBrokerId();
            Long date = getTime(sales.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            DailyBrokerReportDetailDto detail = detailMap.get(detailKey);
            if (detail != null) {
                detail.setSalesAmount(detail.getSalesAmount().add(sales.getTotalPriceWithTax()));
            }
        }
    }

    private void processPaymentAmounts(List<PaymentEntity> paymentList,
                                       Map<String, DailyBrokerReportDetailDto> detailMap) {
        for (PaymentEntity payment : paymentList) {
            Long brokerId = payment.getBrokerId();
            Long date = getTime(payment.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            DailyBrokerReportDetailDto detail = detailMap.get(detailKey);
            if (detail != null) {
                detail.setPaymentAmount(detail.getPaymentAmount().add(payment.getPrice()));
            }
        }
    }

    private void calculateTotalsAndSort(Map<Long, DailyBrokerReportDto> reportMap) {
        for (DailyBrokerReportDto report : reportMap.values()) {
            BigDecimal totalSales = BigDecimal.ZERO;
            BigDecimal totalPayments = BigDecimal.ZERO;
            for (DailyBrokerReportDetailDto detail : report.getDailyDetails()) {
                BigDecimal profitOrLoss = detail.getPaymentAmount().subtract(detail.getSalesAmount());
                detail.setProfitOrLoss(profitOrLoss);
                totalSales = totalSales.add(detail.getSalesAmount());
                totalPayments = totalPayments.add(detail.getPaymentAmount());
            }
            report.setTotalSalesAmount(totalSales);
            report.setTotalPaymentAmount(totalPayments);
            report.setProfitOrLoss(totalPayments.subtract(totalSales));
            report.getDailyDetails().sort(Comparator.comparing(DailyBrokerReportDetailDto::getDate).reversed());
        }
    }

    private List<DailyBrokerReportDto> sortReportsByBrokerName(Map<Long, DailyBrokerReportDto> reportMap) {
        return reportMap.values().stream()
                .sorted(Comparator.comparing(
                        DailyBrokerReportDto::getBrokerFullName,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .toList();
    }

    private List<DailySummaryDto> createSummaryReports(List<SalesEntity> salesList, List<PaymentEntity> paymentList) {
        Map<Long, DailySummaryDto> summaryMap = new HashMap<>();
        processSalesForSummary(salesList, summaryMap);
        processPaymentsForSummary(paymentList, summaryMap);
        calculateSummaryProfitOrLoss(summaryMap);
        return summaryMap.values().stream()
                .sorted(Comparator.comparing(DailySummaryDto::getDate).reversed())
                .toList();
    }

    private void processSalesForSummary(List<SalesEntity> salesList, Map<Long, DailySummaryDto> summaryMap) {
        for (SalesEntity sales : salesList) {
            Long date = getTime(sales.getCreatedDate().toLocalDate());
            DailySummaryDto summary = getOrCreateSummary(date, summaryMap);
            summary.setTotalSalesAmount(summary.getTotalSalesAmount().add(sales.getTotalPriceWithTax()));
        }
    }

    private void processPaymentsForSummary(List<PaymentEntity> paymentList, Map<Long, DailySummaryDto> summaryMap) {
        for (PaymentEntity payment : paymentList) {
            Long date = getTime(payment.getCreatedDate().toLocalDate());
            DailySummaryDto summary = getOrCreateSummary(date, summaryMap);
            summary.setTotalPaymentAmount(summary.getTotalPaymentAmount().add(payment.getPrice()));
        }
    }

    private DailySummaryDto getOrCreateSummary(Long date, Map<Long, DailySummaryDto> summaryMap) {
        return summaryMap.computeIfAbsent(date, k -> {
            DailySummaryDto summary = new DailySummaryDto();
            summary.setDate(date);
            summary.setTotalSalesAmount(BigDecimal.ZERO);
            summary.setTotalPaymentAmount(BigDecimal.ZERO);
            return summary;
        });
    }

    private void calculateSummaryProfitOrLoss(Map<Long, DailySummaryDto> summaryMap) {
        for (DailySummaryDto summary : summaryMap.values()) {
            BigDecimal profitOrLoss = summary.getTotalPaymentAmount().subtract(summary.getTotalSalesAmount());
            summary.setProfitOrLoss(profitOrLoss);
        }
    }

    private DailyTotalDto createTotals(List<SalesEntity> salesList, List<PaymentEntity> paymentList) {
        BigDecimal totalSales = salesList.stream()
                .map(SalesEntity::getTotalPriceWithTax)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPayments = paymentList.stream()
                .map(PaymentEntity::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return DailyTotalDto.builder()
                .totalSalesAmount(totalSales)
                .totalPaymentAmount(totalPayments)
                .profitOrLoss(totalPayments.subtract(totalSales))
                .build();
    }
}