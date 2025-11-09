package com.project.envantra.service;

import com.project.envantra.converter.CategoryConverter;
import com.project.envantra.enums.CategoryStatus;
import com.project.envantra.exception.CategoryNotFoundException;
import com.project.envantra.model.dto.CategoryDto;
import com.project.envantra.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class CategoryGetService {

    private final CategoryRepository categoryRepository;

    public CategoryDto detail(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryConverter::toDto)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAllByCreatorUserIdAndStatusOrderByCreatedDateDesc(getUserId(), CategoryStatus.ACTIVE).stream()
                .map(CategoryConverter::toDto)
                .toList();
    }
}
