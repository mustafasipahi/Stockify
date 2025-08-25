package com.stockify.project.validator;

import com.stockify.project.exception.*;
import com.stockify.project.model.entity.tenant.CategoryEntity;
import com.stockify.project.model.entity.tenant.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.repository.tenant.CategoryRepository;
import com.stockify.project.repository.tenant.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductCreateValidator {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public void validate(ProductCreateRequest request) {
        validateName(request);
        validateCategoryId(request);
    }

    private void validateName(ProductCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new ProductNameException();
        }
        Optional<ProductEntity> product = productRepository.findByName(request.getName());
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }

    private void validateCategoryId(ProductCreateRequest request) {
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        Optional<CategoryEntity> category = categoryRepository.findById(request.getCategoryId());
        if (category.isEmpty()) {
            throw new CategoryNotFoundException(request.getCategoryId());
        }
    }
}
