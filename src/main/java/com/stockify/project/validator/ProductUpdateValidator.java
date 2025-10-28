package com.stockify.project.validator;

import com.stockify.project.enums.CategoryStatus;
import com.stockify.project.exception.CategoryNotFoundException;
import com.stockify.project.exception.ProductNameAlreadyUseException;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import com.stockify.project.service.CategoryGetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Component
@AllArgsConstructor
public class ProductUpdateValidator {

    private final ProductRepository productRepository;
    private final CategoryGetService categoryGetService;

    public void validateName(String productName) {
        Long userId = getUserId();
        Long tenantId = getTenantId();
        Optional<ProductEntity> product = productRepository.findByCreatorUserIdAndNameAndTenantId(userId, productName, tenantId);
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }

    public void validateCategory(Long categoryId) {
        CategoryDto category = categoryGetService.detail(categoryId);
        if (!category.getStatus().equals(CategoryStatus.ACTIVE)) {
            throw new CategoryNotFoundException(categoryId);
        }
    }
}
