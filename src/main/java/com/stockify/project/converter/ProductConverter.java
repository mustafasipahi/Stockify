package com.stockify.project.converter;

import com.stockify.project.model.dto.CategoryDto;
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
        CategoryDto category = getCategory(productEntity.getCategoryId());
        return ProductDto.builder()
                .productId(productEntity.getId())
                .categoryId(productEntity.getCategoryId())
                .categoryName(category.getName())
                .kdv(category.getKdv())
                .inventoryCode(productEntity.getInventoryCode())
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

    private CategoryDto getCategory(Long categoryId) {
        return categoryService.detail(categoryId);
    }
}
