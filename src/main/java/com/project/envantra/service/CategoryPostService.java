package com.project.envantra.service;

import com.project.envantra.enums.CategoryStatus;
import com.project.envantra.exception.CategoryIdException;
import com.project.envantra.exception.CategoryNotFoundException;
import com.project.envantra.exception.HasAvailableProductException;
import com.project.envantra.model.entity.CategoryEntity;
import com.project.envantra.model.request.CategoryCreateRequest;
import com.project.envantra.model.request.CategoryUpdateRequest;
import com.project.envantra.repository.CategoryRepository;
import com.project.envantra.validator.CategoryCreateValidator;
import com.project.envantra.validator.CategoryUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class CategoryPostService {

    private final CategoryRepository categoryRepository;
    private final CategoryCreateValidator createValidator;
    private final CategoryUpdateValidator updateValidator;
    private final ToPassiveService toPassiveService;
    private final ProductGetService productGetService;

    @Transactional
    public void save(CategoryCreateRequest request) {
        createValidator.validate(request);
        CategoryEntity categoryEntity = CategoryEntity.builder()
                .creatorUserId(getUserId())
                .name(request.getName())
                .taxRate(request.getTaxRate())
                .status(CategoryStatus.ACTIVE)
                .build();
        categoryRepository.save(categoryEntity);
    }

    @Transactional
    public void update(CategoryUpdateRequest request) {
        if (request.getCategoryId() == null) {
            throw new CategoryIdException();
        }
        CategoryEntity categoryEntity = categoryRepository.findById(request.getCategoryId())
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
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
        if (hasAvailableProducts(categoryId)) {
            throw new HasAvailableProductException(categoryId);
        }
        categoryEntity.setStatus(CategoryStatus.PASSIVE);
        categoryRepository.save(categoryEntity);
        toPassiveService.updateToPassiveByCategoryId(categoryId);
    }

    private boolean hasAvailableProducts(Long categoryId) {
        return productGetService.hasAvailableProducts(categoryId);
    }
}
