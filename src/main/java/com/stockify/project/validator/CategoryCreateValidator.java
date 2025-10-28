package com.stockify.project.validator;

import com.stockify.project.exception.CategoryNameAlreadyUseException;
import com.stockify.project.exception.CategoryNameException;
import com.stockify.project.exception.TaxRateException;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Component
@AllArgsConstructor
public class CategoryCreateValidator {

    private final CategoryRepository categoryRepository;

    public void validate(CategoryCreateRequest request) {
        if (request.getTaxRate() == null) {
            throw new TaxRateException();
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new CategoryNameException();
        }
        Optional<CategoryEntity> categoryByName = categoryRepository.findByNameAndCreatorUserIdAndTenantId(
                request.getName(),
                getUserId(),
                getTenantId());
        if (categoryByName.isPresent()) {
            throw new CategoryNameAlreadyUseException();
        }
    }
}
