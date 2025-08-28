package com.stockify.project.converter;

import com.stockify.project.model.dto.SalesPrepareDto;
import com.stockify.project.model.dto.SalesPriceDto;
import com.stockify.project.model.entity.InvoiceEntity;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.SalesItemEntity;
import com.stockify.project.model.response.SalesResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesConverter {

    public static SalesPrepareDto toDto(SalesEntity salesEntity, List<SalesItemEntity> salesItems) {
        return SalesPrepareDto.builder()
                .salesEntity(salesEntity)
                .salesItems(salesItems)
                .build();
    }

    public static SalesEntity toEntity(Long brokerId, SalesPriceDto salesPriceDto) {
        return SalesEntity.builder()
                .brokerId(brokerId)
                .totalPriceWithTax(salesPriceDto.getTotalPriceWithTax())
                .subtotalPrice(salesPriceDto.getSubtotalPrice())
                .discountRate(salesPriceDto.getDiscountRate())
                .discountPrice(salesPriceDto.getDiscountPrice())
                .totalPrice(salesPriceDto.getTotalPrice())
                .tenantId(getTenantId())
                .build();
    }

    public static SalesResponse toResponse(SalesEntity salesEntity, List<SalesItemEntity> salesItems, InvoiceEntity invoiceEntity) {
        return SalesResponse.builder()
                .salesId(salesEntity.getId())
                .salesItems(SalesItemConverter.toDto(salesItems))
                .totalPriceWithTax(salesEntity.getTotalPriceWithTax())
                .subtotalPrice(salesEntity.getSubtotalPrice())
                .totalPrice(salesEntity.getTotalPrice())
                .discountPrice(salesEntity.getDiscountPrice())
                .discountRate(salesEntity.getDiscountRate())
                .createdDate(salesEntity.getCreatedDate())
                .invoiceId(Optional.ofNullable(invoiceEntity)
                        .map(InvoiceEntity::getId)
                        .orElse(null))
                .build();
    }
}
