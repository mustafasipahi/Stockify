package com.stockify.project.service;

import com.stockify.project.enums.CategoryStatus;
import com.stockify.project.exception.CategoryIdException;
import com.stockify.project.exception.CategoryNotFoundException;
import com.stockify.project.model.entity.CategoryEntity;
import com.stockify.project.model.request.CategoryCreateRequest;
import com.stockify.project.model.request.CategoryUpdateRequest;
import com.stockify.project.repository.CategoryRepository;
import com.stockify.project.validator.CategoryCreateValidator;
import com.stockify.project.validator.CategoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class CategoryPostService {

    private final CategoryRepository categoryRepository;
    private final CategoryCreateValidator createValidator;
    private final CategoryUpdateValidator updateValidator;
    private final ToPassiveService toPassiveService;

    @Transactional
    public void save(CategoryCreateRequest request) {
        createValidator.validate(request);
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .name(request.getName())
                .taxRate(request.getTaxRate())
                .tenantId(getTenantId())
                .status(CategoryStatus.ACTIVE)
                .build();
        categoryRepository.save(categoryEntity);
    }

    @Transactional
    public void update(CategoryUpdateRequest request) {
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        CategoryEntity categoryEntity = categoryRepository.findByIdAndTenantId(request.getCategoryId(), getTenantId())
                .orElseThrow(() -> new CategoryNotFoundException(request.getCategoryId()));
        if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(categoryEntity.getName())) {
            updateValidator.validateName(request.getName());
            categoryEntity.setName(request.getName());
        }
        if (request.getTaxRate() != null) {
            categoryEntity.setTaxRate(request.getTaxRate());
        }
        categoryRepository.save(categoryEntity);
    }

    @Transactional
    public void delete(Long categoryId) {
        if (categoryId == null) {
            throw new CategoryIdException();
        }
        CategoryEntity categoryEntity = categoryRepository.findByIdAndTenantId(categoryId, getTenantId())
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        categoryEntity.setStatus(CategoryStatus.PASSIVE);
        categoryRepository.save(categoryEntity);
        toPassiveService.updateToPassiveByCategoryId(categoryId);
    }
}
