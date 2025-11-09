package com.project.envantra.validator;

import com.project.envantra.enums.CategoryStatus;
import com.project.envantra.exception.CategoryIdException;
import com.project.envantra.exception.CategoryNotFoundException;
import com.project.envantra.exception.ProductNameAlreadyUseException;
import com.project.envantra.exception.ProductNameException;
import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.model.request.ProductCreateRequest;
import com.project.envantra.repository.ProductRepository;
import com.project.envantra.service.CategoryGetService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.project.envantra.util.LoginContext.getUserId;

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
