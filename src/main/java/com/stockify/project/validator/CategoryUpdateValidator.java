package com.stockify.project.validator;

import com.stockify.project.exception.CategoryNameAlreadyUseException;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.util.LoginContext.getUserId;

@Component
@AllArgsConstructor
public class CategoryUpdateValidator {

    private final CategoryRepository categoryRepository;

    public void validateName(String name) {
        Optional<CategoryEntity> categoryByName = categoryRepository.findByNameAndCreatorUserId(name, getUserId());
        if (categoryByName.isPresent()) {
            throw new CategoryNameAlreadyUseException();
        }
    }
}
