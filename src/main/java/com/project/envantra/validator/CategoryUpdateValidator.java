package com.project.envantra.validator;

import com.project.envantra.exception.CategoryNameAlreadyUseException;
import com.project.envantra.model.entity.CategoryEntity;
import com.project.envantra.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.project.envantra.util.LoginContext.getUserId;

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
