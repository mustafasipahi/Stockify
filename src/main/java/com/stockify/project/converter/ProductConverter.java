package com.stockify.project.converter;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductConverter {

    public static ProductDto toDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .stockCode(productEntity.getStockCode())
                .name(productEntity.getName())
                .price(productEntity.getPrice())
                .createdAgentId(productEntity.getCreatedAgentId())
                .updatedAgentId(productEntity.getUpdatedAgentId())
                .createdDate(productEntity.getCreatedDate())
                .build();
    }

    public static ProductDto toIdDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .build();
    }
}
