package com.stockify.project.converter;

import com.stockify.project.model.dto.SalesItemDto;
import com.stockify.project.model.entity.SalesItemEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SalesItemConverter {

    public static List<SalesItemDto> toDto(List<SalesItemEntity> salesItems) {
        if (CollectionUtils.isEmpty(salesItems)) {
            return Collections.emptyList();
        }
        return salesItems.stream()
                .map(salesItem -> SalesItemDto.builder()
                        .id(salesItem.getId())
                        .salesId(salesItem.getSalesId())
                        .productId(salesItem.getProductId())
                        .unitPrice(salesItem.getUnitPrice())
                        .totalPrice(salesItem.getTotalPrice())
                        .productCount(salesItem.getProductCount())
                        .createdDate(salesItem.getCreatedDate())
                        .build())
                .toList();
    }
}
