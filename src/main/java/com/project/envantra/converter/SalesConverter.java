package com.project.envantra.converter;

import com.project.envantra.model.dto.*;
import com.project.envantra.model.entity.SalesEntity;
import com.project.envantra.model.entity.SalesItemEntity;
import com.project.envantra.model.response.SalesResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static com.project.envantra.util.DateUtil.getTime;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesConverter {

    public static SalesPrepareDto toPrepareDto(SalesDto sales, List<SalesItemDto> salesItems, BrokerDto broker) {
        return SalesPrepareDto.builder()
                .sales(sales)
                .salesItems(salesItems)
                .broker(broker)
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
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static SalesItemDto toSalesItemDto(Long productId,
                                              Integer productCount,
                                              BigDecimal unitPrice,
                                              BigDecimal totalPrice,
                                              BigDecimal taxRate,
                                              BigDecimal taxPrice,
                                              BigDecimal totalPriceWithTax,
                                              String productName,
                                              BigDecimal discountRate,
                                              BigDecimal discountPrice,
                                              BigDecimal priceAfterDiscount) {
        return SalesItemDto.builder()
                .productId(productId)
                .productName(productName)
                .productCount(productCount)
                .unitPrice(unitPrice)
                .totalPrice(totalPrice)
                .discountRate(discountRate)
                .discountPrice(discountPrice)
                .priceAfterDiscount(priceAfterDiscount)
                .taxRate(taxRate)
                .taxPrice(taxPrice)
                .totalPriceWithTax(totalPriceWithTax)
                .build();
    }

    public static SalesEntity toSalesEntity(SalesDto sales) {
        return SalesEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(sales.getBrokerId())
                .documentId(sales.getDocumentId())
                .subtotalPrice(sales.getSubtotalPrice())
                .discountRate(sales.getDiscountRate())
                .discountPrice(sales.getDiscountPrice())
                .totalPrice(sales.getTotalPrice())
                .totalTaxPrice(sales.getTotalTaxPrice())
                .totalPriceWithTax(sales.getTotalPriceWithTax())
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
                        .discountRate(salesItem.getDiscountRate())
                        .discountPrice(salesItem.getDiscountPrice())
                        .priceAfterDiscount(salesItem.getPriceAfterDiscount())
                        .taxRate(salesItem.getTaxRate())
                        .taxPrice(salesItem.getTaxPrice())
                        .totalPriceWithTax(salesItem.getTotalPriceWithTax())
                        .build())
                .toList();
    }

    public static SalesResponse toResponse(SalesDto sales, List<SalesItemDto> salesItems,
                                           String downloadUrl, String invoiceDownloadUrl) {
        return SalesResponse.builder()
                .salesId(sales.getId())
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
}
