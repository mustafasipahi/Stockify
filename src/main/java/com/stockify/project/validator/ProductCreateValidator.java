package com.stockify.project.validator;

import com.stockify.project.enums.CategoryStatus;
import com.stockify.project.exception.*;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.repository.ProductRepository;
import com.stockify.project.service.CategoryGetService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.util.LoginContext.getUserId;

@Component
@AllArgsConstructor
public class ProductCreateValidator {

    private final ProductRepository productRepository;
    private final CategoryGetService categoryGetService;

    public void validate(ProductCreateRequest request) {
        validateName(request);
        validateCategory(request);
    }

    private void validateName(ProductCreateRequest request) {
        if (StringUtils.isBlank(request.getName())) {
            throw new ProductNameException();
        }
        Long userId = getUserId();
        Optional<ProductEntity> product = productRepository.findByCreatorUserIdAndName(userId, request.getName());
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException(product.get().getId());
        }
    }

    private void validateCategory(ProductCreateRequest request) {
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        CategoryDto category = categoryGetService.detail(request.getCategoryId());
        if (!category.getStatus().equals(CategoryStatus.ACTIVE)) {
            throw new CategoryNotFoundException(request.getCategoryId());
        }
    }
}
