package com.stockify.project.converter;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.tenant.ProductEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductConverter {

    public static ProductDto toDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .categoryId(productEntity.getCategoryId())
                .stockCode(productEntity.getStockCode())
                .name(productEntity.getName())
                .status(productEntity.getStatus())
                .createdDate(productEntity.getCreatedDate())
                .lastModifiedDate(productEntity.getLastModifiedDate())
                .build();
    }

    public static ProductDto toIdDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .build();
    }
}
