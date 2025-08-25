package com.stockify.project.validator;

import com.stockify.project.exception.CategoryNameAlreadyUseException;
import com.stockify.project.exception.CategoryNameException;
import com.stockify.project.exception.KdvException;
import com.stockify.project.model.entity.tenant.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.repository.tenant.CategoryRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class CategoryCreateValidator {

    private final CategoryRepository categoryRepository;

    public void validate(CategoryCreateRequest request) {
        if (request.getKdv() == null) {
            throw new KdvException();
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new CategoryNameException();
        }
        Optional<CategoryEntity> categoryByName = categoryRepository.findByName(request.getName());
        if (categoryByName.isPresent()) {
            throw new CategoryNameAlreadyUseException();
        }
    }
}
