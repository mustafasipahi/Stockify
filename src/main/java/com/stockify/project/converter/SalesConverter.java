package com.stockify.project.converter;

import com.stockify.project.model.dto.SalesDto;
import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.dto.SalesPriceDto;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.SalesItemEntity;
import com.stockify.project.model.response.SalesResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesConverter {

    public static SalesPrepareDto toPrepareDto(SalesDto sales, List<SalesItemDto> salesItems) {
        return SalesPrepareDto.builder()
                .sales(sales)
                .salesItems(salesItems)
                .build();
    }

    public static SalesDto toSalesDto(Long brokerId, SalesPriceDto salesPriceDto) {
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

    public static SalesEntity toSalesEntity(SalesDto sales) {
        return SalesEntity.builder()
                .brokerId(sales.getBrokerId())
                .documentId(sales.getDocumentId())
                .documentNumber(sales.getDocumentNumber())
                .subtotalPrice(sales.getSubtotalPrice())
                .discountRate(sales.getDiscountRate())
                .discountPrice(sales.getDiscountPrice())
                .totalPrice(sales.getTotalPrice())
                .totalTaxPrice(sales.getTotalTaxPrice())
                .totalPriceWithTax(sales.getTotalPriceWithTax())
                .tenantId(getTenantId())
                .build();
    }

    public static List<SalesItemEntity> toSalesItemEntity(List<SalesItemDto> salesItems) {
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

    public static SalesResponse toResponse(SalesDto sales, List<SalesItemDto> salesItems, String documentId) {
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
                .downloadUrl(getDownloadUrl(documentId))
                .createdDate(getTime(sales.getCreatedDate()))
                .build();
    }
}
