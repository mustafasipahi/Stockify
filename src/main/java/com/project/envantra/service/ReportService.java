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
import static com.project.envantra.util.LoginContext.getUserId;
import static com.project.envantra.util.NameUtil.getBrokerFullName;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final BrokerGetService brokerGetService;
    private final SalesService salesService;
    private final PaymentService paymentService;
    private final BrokerVisitService brokerVisitService;

    public DailyReportResponse getDailyReport(DailyReportRequest request) {
        Map<Long, BrokerDto> brokerMap = getBrokerMap(request.getBrokerId());
        if (brokerMap.isEmpty()) {
            return createEmptyResponse();
        }
        LocalDateTime startDate = getStartDate(request.getStartDate());
        LocalDateTime endDate = getEndDate(request.getEndDate());
        List<SalesEntity> salesList = salesService.findAllByBrokerId(brokerMap.keySet()).stream()
                .filter(sales -> isDateInRange(sales.getCreatedDate(), startDate, endDate))
                .toList();
        List<PaymentEntity> paymentList = paymentService.findAllByBrokerId(brokerMap.keySet()).stream()
                .filter(payment -> isDateInRange(payment.getCreatedDate(), startDate, endDate))
                .toList();
        List<BrokerVisitDto> visitInfoList = brokerVisitService.getVisitInfoListByDate(startDate, endDate);
        return DailyReportResponse.builder()
                .dailyBrokerReports(createBrokerReports(salesList, paymentList, visitInfoList, brokerMap))
                .dailySummaryReports(createSummaryReports(salesList, paymentList))
                .totals(createTotals(salesList, paymentList))
                .build();
    }

    private Map<Long, BrokerDto> getBrokerMap(Long brokerId) {
        if (brokerId != null) {
            BrokerDto activeBroker = brokerGetService.getActiveBroker(brokerId);
            return Objects.equals(activeBroker.getCreatorUserId(), getUserId())
                    ? Collections.singletonMap(brokerId, activeBroker)
                    : Collections.emptyMap();
        } else {
            List<BrokerDto> activeBrokers = brokerGetService.getAllBrokers();
            return activeBrokers.stream()
                    .collect(Collectors.toMap(BrokerDto::getBrokerId, broker -> broker));
        }
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

    private List<DailyBrokerReportDto> createBrokerReports(List<SalesEntity> salesList, List<PaymentEntity> paymentList,
                                                           List<BrokerVisitDto> visitInfoList, Map<Long, BrokerDto> brokerMap) {
        Map<Long, DailyBrokerReportDto> reportMap = new HashMap<>();
        Map<String, DailyBrokerReportDetailDto> detailMap = new HashMap<>();
        Map<String, BrokerVisitDto> visitInfoMap = visitInfoList.stream()
                .collect(Collectors.toMap(visit -> visit.getBrokerId() + "_" + visit.getVisitDate(), visit -> visit));
        for (SalesEntity sales : salesList) {
            Long brokerId = sales.getBrokerId();
            Long date = getTime(sales.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            DailyBrokerReportDto report = reportMap.get(brokerId);
            if (report == null) {
                BrokerDto broker = brokerMap.get(brokerId);
                report = new DailyBrokerReportDto();
                report.setOrderNo(broker.getOrderNo());
                report.setBrokerFullName(getBrokerFullName(broker));
                report.setDailyDetails(new ArrayList<>());
                report.setTotalSalesAmount(BigDecimal.ZERO);
                report.setTotalPaymentAmount(BigDecimal.ZERO);
                reportMap.put(brokerId, report);
            }
            DailyBrokerReportDetailDto detail = detailMap.get(detailKey);
            if (detail == null) {
                detail = new DailyBrokerReportDetailDto();
                detail.setDate(date);
                detail.setSalesAmount(BigDecimal.ZERO);
                detail.setPaymentAmount(BigDecimal.ZERO);
                detail.setVisitInfo(visitInfoMap.get(detailKey));
                report.getDailyDetails().add(detail);
                detailMap.put(detailKey, detail);
            }
            detail.setSalesAmount(detail.getSalesAmount().add(sales.getTotalPriceWithTax()));
        }
        for (PaymentEntity payment : paymentList) {
            Long brokerId = payment.getBrokerId();
            Long date = getTime(payment.getCreatedDate().toLocalDate());
            String detailKey = brokerId + "_" + date;
            DailyBrokerReportDto report = reportMap.get(brokerId);
            if (report == null) {
                BrokerDto broker = brokerMap.get(brokerId);
                report = new DailyBrokerReportDto();
                report.setOrderNo(broker.getOrderNo());
                report.setBrokerFullName(getBrokerFullName(broker));
                report.setDailyDetails(new ArrayList<>());
                report.setTotalSalesAmount(BigDecimal.ZERO);
                report.setTotalPaymentAmount(BigDecimal.ZERO);
                reportMap.put(brokerId, report);
            }
            DailyBrokerReportDetailDto detail = detailMap.get(detailKey);
            if (detail == null) {
                detail = new DailyBrokerReportDetailDto();
                detail.setDate(date);
                detail.setSalesAmount(BigDecimal.ZERO);
                detail.setPaymentAmount(BigDecimal.ZERO);
                detail.setVisitInfo(visitInfoMap.get(detailKey));
                report.getDailyDetails().add(detail);
                detailMap.put(detailKey, detail);
            }
            detail.setPaymentAmount(detail.getPaymentAmount().add(payment.getPrice()));
        }
        for (BrokerVisitDto visitInfo : visitInfoList) {
            Long brokerId = visitInfo.getBrokerId();
            Long date = visitInfo.getVisitDate();
            String detailKey = brokerId + "_" + date;
            if (!detailMap.containsKey(detailKey)) {
                DailyBrokerReportDto report = reportMap.get(brokerId);
                if (report == null) {
                    BrokerDto broker = brokerMap.get(brokerId);
                    report = new DailyBrokerReportDto();
                    report.setOrderNo(broker.getOrderNo());
                    report.setBrokerFullName(getBrokerFullName(broker));
                    report.setDailyDetails(new ArrayList<>());
                    report.setTotalSalesAmount(BigDecimal.ZERO);
                    report.setTotalPaymentAmount(BigDecimal.ZERO);
                    reportMap.put(brokerId, report);
                }
                DailyBrokerReportDetailDto detail = new DailyBrokerReportDetailDto();
                detail.setDate(date);
                detail.setSalesAmount(BigDecimal.ZERO);
                detail.setPaymentAmount(BigDecimal.ZERO);
                detail.setVisitInfo(visitInfo);
                report.getDailyDetails().add(detail);
                detailMap.put(detailKey, detail);
            }
        }
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
        return reportMap.values().stream()
                .sorted(Comparator.comparing(DailyBrokerReportDto::getOrderNo))
                .toList();
    }

    private List<DailySummaryDto> createSummaryReports(List<SalesEntity> salesList, List<PaymentEntity> paymentList) {
        Map<Long, DailySummaryDto> summaryMap = new HashMap<>();
        for (SalesEntity sales : salesList) {
            Long date = getTime(sales.getCreatedDate().toLocalDate());
            DailySummaryDto summary = summaryMap.get(date);
            if (summary == null) {
                summary = new DailySummaryDto();
                summary.setDate(date);
                summary.setTotalSalesAmount(BigDecimal.ZERO);
                summary.setTotalPaymentAmount(BigDecimal.ZERO);
                summaryMap.put(date, summary);
            }
            summary.setTotalSalesAmount(summary.getTotalSalesAmount().add(sales.getTotalPriceWithTax()));
        }
        for (PaymentEntity payment : paymentList) {
            Long date = getTime(payment.getCreatedDate().toLocalDate());
            DailySummaryDto summary = summaryMap.get(date);
            if (summary == null) {
                summary = new DailySummaryDto();
                summary.setDate(date);
                summary.setTotalSalesAmount(BigDecimal.ZERO);
                summary.setTotalPaymentAmount(BigDecimal.ZERO);
                summaryMap.put(date, summary);
            }
            summary.setTotalPaymentAmount(summary.getTotalPaymentAmount().add(payment.getPrice()));
        }
        for (DailySummaryDto summary : summaryMap.values()) {
            BigDecimal profitOrLoss = summary.getTotalPaymentAmount().subtract(summary.getTotalSalesAmount());
            summary.setProfitOrLoss(profitOrLoss);
        }
        return summaryMap.values().stream()
                .sorted(Comparator.comparing(DailySummaryDto::getDate).reversed())
                .toList();
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
