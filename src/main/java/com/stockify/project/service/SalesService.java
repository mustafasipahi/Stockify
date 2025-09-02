package com.stockify.project.service;

import com.stockify.project.model.dto.ReceiptInfoDto;
import com.stockify.project.model.dto.SalesProductDto;
import com.stockify.project.model.request.SalesRequest;
import com.stockify.project.model.response.SalesConfirmResponse;
import com.stockify.project.model.response.SalesPreviewResponse;
import com.stockify.project.model.response.SalesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesPersistenceService salesPersistenceService;
    private final ReceiptInfoService receiptInfoService;
    private final BalanceService balanceService;

    public SalesResponse salesCalculate(SalesRequest request) {
        return salesPersistenceService.salesPreview(request);
    }

    public SalesPreviewResponse salesPreview(SalesRequest request) {
        SalesResponse salesResponse = salesPersistenceService.salesPreview(request);
        return SalesPreviewResponse.builder()
                .receiptInfo(getReceiptInfo(request.getBrokerId(), salesResponse))
                .salesResponse(salesResponse)
                .build();
    }

    public SalesConfirmResponse salesConfirm(SalesRequest request) {
        SalesResponse salesResponse = salesPersistenceService.salesConfirm(request);
        return SalesConfirmResponse.builder()
                .receiptInfo(getReceiptInfo(request.getBrokerId(), salesResponse))
                .salesResponse(salesResponse)
                .build();
    }

    public List<SalesProductDto> getProducts() {
        return salesPersistenceService.getProducts();
    }

    public ReceiptInfoDto getReceiptInfo(Long brokerId, SalesResponse salesResponse) {
        Long tenantId = getTenantId();
        ReceiptInfoDto receiptInfo = receiptInfoService.getReceiptInfo(tenantId);
        BigDecimal brokerCurrentBalance = balanceService.getBrokerCurrentBalance(brokerId, tenantId);
        receiptInfo.setDocumentNumber(salesResponse.getDocumentNumber());
        receiptInfo.setDocumentDate(salesResponse.getCreatedDate());
        receiptInfo.setOldBalance(brokerCurrentBalance);
        receiptInfo.setNewBalance(brokerCurrentBalance.add(salesResponse.getTotalPriceWithTax()));
        return receiptInfo;
    }
}
