package com.stockify.project.converter;

import com.stockify.project.model.dto.*;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.SalesItemEntity;
import com.stockify.project.model.response.SalesResponse;
import com.stockify.project.repository.SalesRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.stockify.project.constant.DocumentNumberConstants.SALES_DEFAULT;
import static com.stockify.project.constant.DocumentNumberConstants.SALES_PREFIX;
import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class SalesConverter {

    private final SalesRepository salesRepository;

    public SalesPrepareDto toPrepareDto(SalesDto sales, List<SalesItemDto> salesItems, BrokerDto broker) {
        return SalesPrepareDto.builder()
                .sales(sales)
                .salesItems(salesItems)
                .broker(broker)
                .build();
    }

    public SalesDto toSalesDto(Long brokerId, SalesPriceDto salesPriceDto) {
        return SalesDto.builder()
                .brokerId(brokerId)
                .subtotalPrice(salesPriceDto.getSubtotalPrice())
                .discountRate(salesPriceDto.getDiscountRate())
                .discountPrice(salesPriceDto.getDiscountPrice())
                .totalPrice(salesPriceDto.getTotalPrice())
                .totalTaxPrice(salesPriceDto.getTotalTaxPrice())
                .totalPriceWithTax(salesPriceDto.getTotalPriceWithTax())
                .build();
    }

    public static SalesItemDto toSalesItemDto(Long productId, Integer productCount, BigDecimal unitPrice, BigDecimal totalPrice,
                                              BigDecimal taxRate, BigDecimal taxPrice, BigDecimal totalPriceWithTax, String productName) {
        return SalesItemDto.builder()
                .productId(productId)
                .productName(productName)
                .productCount(productCount)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .taxRate(taxRate)
                .taxPrice(taxPrice)
                .totalPriceWithTax(totalPriceWithTax)
                .build();
    }

    public SalesEntity toSalesEntity(SalesDto sales) {
        String documentNumber = getDocumentNumber();
        sales.setDocumentNumber(documentNumber);
        return SalesEntity.builder()
                .brokerId(sales.getBrokerId())
                .documentId(sales.getDocumentId())
                .documentNumber(documentNumber)
                .subtotalPrice(sales.getSubtotalPrice())
                .discountRate(sales.getDiscountRate())
                .discountPrice(sales.getDiscountPrice())
                .totalPrice(sales.getTotalPrice())
                .totalTaxPrice(sales.getTotalTaxPrice())
                .totalPriceWithTax(sales.getTotalPriceWithTax())
                .tenantId(getTenantId())
                .build();
    }

    public List<SalesItemEntity> toSalesItemEntity(List<SalesItemDto> salesItems) {
        if (CollectionUtils.isEmpty(salesItems)) {
            return Collections.emptyList();
        }
        return salesItems.stream()
                .map(salesItem -> SalesItemEntity.builder()
                        .salesId(salesItem.getSalesId())
                        .productId(salesItem.getProductId())
                        .productCount(salesItem.getProductCount())
                        .unitPrice(salesItem.getUnitPrice())
                        .totalPrice(salesItem.getTotalPrice())
                        .taxRate(salesItem.getTaxRate())
                        .taxPrice(salesItem.getTaxPrice())
                        .totalPriceWithTax(salesItem.getTotalPriceWithTax())
                        .tenantId(getTenantId())
                        .build())
                .toList();
    }

    public SalesResponse toResponse(SalesDto sales, List<SalesItemDto> salesItems,
                                    String downloadUrl, String invoiceDownloadUrl) {
        return SalesResponse.builder()
                .salesId(sales.getId())
                .documentNumber(sales.getDocumentNumber())
                .salesItems(salesItems)
                .subtotalPrice(sales.getSubtotalPrice())
                .discountRate(sales.getDiscountRate())
                .discountPrice(sales.getDiscountPrice())
                .totalPrice(sales.getTotalPrice())
                .totalTaxPrice(sales.getTotalTaxPrice())
                .totalPriceWithTax(sales.getTotalPriceWithTax())
                .downloadUrl(downloadUrl)
                .invoiceDownloadUrl(invoiceDownloadUrl)
                .createdDate(getTime(sales.getCreatedDate()))
                .build();
    }

    private String getDocumentNumber() {
        return Optional.ofNullable(salesRepository.findMaxDocumentNumberNumeric())
                .map(lastDocumentNumber -> SALES_PREFIX + (lastDocumentNumber + 1))
                .orElse(SALES_PREFIX + SALES_DEFAULT);
    }
}
