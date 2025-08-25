package com.stockify.project.converter;

import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.service.CategoryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ProductConverter {

    private final CategoryService categoryService;

    public ProductDto toDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .categoryId(productEntity.getCategoryId())
                .categoryName(getCategoryName(productEntity.getCategoryId()))
                .stockCode(productEntity.getStockCode())
                .name(productEntity.getName())
                .status(productEntity.getStatus())
                .createdDate(productEntity.getCreatedDate())
                .lastModifiedDate(productEntity.getLastModifiedDate())
                .build();
    }

    public ProductDto toIdDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .build();
    }

    private String getCategoryName(Long categoryId) {
        return categoryService.detail(categoryId).getName();
    }
}
