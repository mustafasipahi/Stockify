package com.project.envantra.converter;

import com.project.envantra.enums.ProductStatus;
import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.model.dto.ProductDto;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.model.request.ProductCreateRequest;
import com.project.envantra.service.CategoryGetService;
import com.project.envantra.generator.InventoryCodeGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import static com.project.envantra.util.DateUtil.getTime;
import static com.project.envantra.util.LoginContext.getUserId;

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
