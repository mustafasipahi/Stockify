package com.project.envantra.validator;

import com.project.envantra.exception.CategoryNameAlreadyUseException;
import com.project.envantra.exception.CategoryNameException;
import com.project.envantra.exception.TaxRateException;
import com.project.envantra.model.entity.CategoryEntity;
import com.project.envantra.model.request.CategoryCreateRequest;
import com.project.envantra.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.project.envantra.util.LoginContext.getUserId;

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
        Optional<CategoryEntity> categoryByName = categoryRepository.findByNameAndCreatorUserId(request.getName(), getUserId());
        if (categoryByName.isPresent()) {
            throw new CategoryNameAlreadyUseException();
        }
    }
}
