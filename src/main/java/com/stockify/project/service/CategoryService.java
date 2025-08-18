package com.stockify.project.service;

import com.stockify.project.converter.CategoryConverter;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.repository.CategoryRepository;
import com.stockify.project.validator.CategoryCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCreateValidator createValidator;

    public void save(CategoryCreateRequest request) {
        createValidator.validate(request);
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(request.getName())
                .build();
        categoryRepository.save(categoryEntity);
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAllByOrderByNameAsc().stream()
                .map(CategoryConverter::toDto)
                .toList();
    }
}
