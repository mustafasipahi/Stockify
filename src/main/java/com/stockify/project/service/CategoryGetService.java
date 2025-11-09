package com.stockify.project.service;

import com.stockify.project.converter.CategoryConverter;
import com.stockify.project.enums.CategoryStatus;
import com.stockify.project.exception.CategoryNotFoundException;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stockify.project.util.LoginContext.getUserId;

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
