package com.project.envantra.validator;

import com.project.envantra.enums.CategoryStatus;
import com.project.envantra.exception.CategoryNotFoundException;
import com.project.envantra.exception.ProductNameAlreadyUseException;
import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.repository.ProductRepository;
import com.project.envantra.service.CategoryGetService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.project.envantra.util.LoginContext.getUserId;

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
