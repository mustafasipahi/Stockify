package com.stockify.project.service;

import com.stockify.project.converter.CategoryConverter;
import com.stockify.project.exception.CategoryNotFoundException;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.repository.CategoryRepository;
import com.stockify.project.validator.CategoryCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCreateValidator createValidator;

    public void save(CategoryCreateRequest request) {
        createValidator.validate(request);
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(request.getName())
                .kdv(request.getKdv())
                .tenantId(getTenantId())
                .build();
        categoryRepository.save(categoryEntity);
    }

    public CategoryDto detail(Long categoryId) {
        return categoryRepository.findByIdAndTenantId(categoryId, getTenantId())
                .map(CategoryConverter::toDto)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    public List<CategoryDto> getAll() {
        return categoryRepository.findAllByTenantIdOrderByNameAsc(getTenantId()).stream()
                .map(CategoryConverter::toDto)
                .toList();
    }
}
