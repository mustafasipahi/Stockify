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

import static com.stockify.project.util.LoginContext.getUserId;

@Component
@AllArgsConstructor
public class ProductUpdateValidator {

    private final ProductRepository productRepository;
    private final CategoryGetService categoryGetService;

    public void validateName(Long id, String productName) {
        Long userId = getUserId();
        Optional<ProductEntity> product = productRepository.findByCreatorUserIdAndName(userId, productName);
        if (product.isPresent() && !product.get().getId().equals(id)) {
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
