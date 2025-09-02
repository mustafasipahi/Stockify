package com.stockify.project.service;

import com.stockify.project.converter.CategoryConverter;
import com.stockify.project.exception.CategoryIdException;
import com.stockify.project.exception.CategoryNotFoundException;
import com.stockify.project.model.dto.CategoryDto;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.model.request.CategoryUpdateRequest;
import com.stockify.project.repository.CategoryRepository;
import com.stockify.project.validator.CategoryCreateValidator;
import com.stockify.project.validator.CategoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stockify.project.constant.CacheConstants.CATEGORY_DETAIL;
import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryCreateValidator createValidator;
    private final CategoryUpdateValidator updateValidator;

    @Transactional
    public void save(CategoryCreateRequest request) {
        createValidator.validate(request);
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(request.getName())
                .taxRate(request.getTaxRate())
                .tenantId(getTenantId())
                .build();
        categoryRepository.save(categoryEntity);
    }

    @Transactional
    @CacheEvict(value = CATEGORY_DETAIL, key = "#request.categoryId")
    public void update(CategoryUpdateRequest request) {
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        CategoryEntity categoryEntity = categoryRepository.findByIdAndTenantId(request.getCategoryId(), getTenantId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        if (StringUtils.isNotBlank(request.getName())) {
            updateValidator.validateName(request.getName());
            categoryEntity.setName(request.getName());
        }
        if (request.getTaxRate() != null) {
            categoryEntity.setTaxRate(request.getTaxRate());
        }
        categoryRepository.save(categoryEntity);
    }

    @Cacheable(value = CATEGORY_DETAIL, key = "#categoryId")
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
