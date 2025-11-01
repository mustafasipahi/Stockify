package com.stockify.project.converter;

import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.service.CategoryGetService;
import com.stockify.project.generator.InventoryCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Component
@AllArgsConstructor
public class ProductConverter {

    private final CategoryGetService categoryGetService;
    private final InventoryCodeGenerator inventoryCodeGenerator;

    public ProductEntity toEntity(ProductCreateRequest request) {
        return ProductEntity.builder()
                .categoryId(request.getCategoryId())
                .creatorUserId(getUserId())
                .inventoryCode(inventoryCodeGenerator.generateInventoryCode())
                .name(request.getName())
                .status(ProductStatus.ACTIVE)
                .tenantId(getTenantId())
                .build();
    }

    public ProductDto toDto(ProductEntity productEntity) {
        CategoryDto category = getCategory(productEntity.getCategoryId());
        return ProductDto.builder()
                .productId(productEntity.getId())
                .categoryId(productEntity.getCategoryId())
                .creatorUserId(productEntity.getCreatorUserId())
                .categoryName(category.getName())
                .taxRate(category.getTaxRate())
                .inventoryCode(productEntity.getInventoryCode())
                .name(productEntity.getName())
                .status(productEntity.getStatus())
                .createdDate(getTime(productEntity.getCreatedDate()))
                .lastModifiedDate(getTime(productEntity.getLastModifiedDate()))
                .build();
    }

    public ProductDto toIdDto(ProductEntity productEntity) {
        return ProductDto.builder()
                .productId(productEntity.getId())
                .build();
    }

    private CategoryDto getCategory(Long categoryId) {
        return categoryGetService.detail(categoryId);
    }
}
