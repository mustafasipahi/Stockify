package com.stockify.project.validator;

import com.stockify.project.exception.*;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.repository.CategoryRepository;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductCreateValidator {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public void validate(ProductCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new ProductNameException();
        }
        validateName(request.getName());
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        validateCategoryId(request.getCategoryId());
        if (request.getPrice() == null) {
            throw new ProductPriceException();
        }
        if (request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductPriceException();
        }
    }

    private void validateName(String productName) {
        Optional<ProductEntity> product = productRepository.findByName(productName);
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }

    private void validateCategoryId(Long categoryId) {
        Optional<CategoryEntity> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(categoryId);
        }
    }
}
