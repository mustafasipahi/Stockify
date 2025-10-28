package com.stockify.project.service;

import com.stockify.project.converter.ProductConverter;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.exception.HasAvailableInventoryException;
import com.stockify.project.exception.ProductIdException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.model.request.ProductUpdateRequest;
import com.stockify.project.repository.ProductRepository;
import com.stockify.project.validator.ProductCreateValidator;
import com.stockify.project.validator.ProductUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class ProductPostService {

    private final ProductRepository productRepository;
    private final ProductCreateValidator createValidator;
    private final ProductUpdateValidator updateValidator;
    private final ProductConverter productConverter;
    private final InventoryPostService inventoryPostService;
    private final InventoryGetService inventoryGetService;
    private final ToPassiveService toPassiveService;

    @Transactional
    public ProductDto save(ProductCreateRequest request) {
        createValidator.validate(request);
        ProductEntity product = productConverter.toEntity(request);
        ProductEntity savedProduct = productRepository.save(product);
        inventoryPostService.saveDefault(savedProduct.getId());
        return productConverter.toIdDto(savedProduct);
    }

    @Transactional
    public ProductDto update(ProductUpdateRequest request) {
        if (request.getProductId() == null) {
            throw new ProductIdException();
        }
        ProductEntity productEntity = productRepository.findByIdAndTenantId(request.getProductId(), getTenantId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
        if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(productEntity.getName())) {
            updateValidator.validateName(request.getName());
            productEntity.setName(request.getName());
        }
        if (request.getCategoryId() != null) {
            updateValidator.validateCategory(request.getCategoryId());
            productEntity.setCategoryId(request.getCategoryId());
        }
        ProductEntity updatedProduct = productRepository.save(productEntity);
        return productConverter.toIdDto(updatedProduct);
    }

    @Transactional
    public ProductDto delete(Long productId) {
        ProductEntity productEntity = productRepository.findByIdAndTenantId(productId, getTenantId())
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (hasAvailableInventory(productId)) {
            throw new HasAvailableInventoryException(productId);
        }
        productEntity.setStatus(ProductStatus.PASSIVE);
        ProductEntity deletedProduct = productRepository.save(productEntity);
        toPassiveService.updateToPassiveByProductId(productId);
        return productConverter.toIdDto(deletedProduct);
    }

    private boolean hasAvailableInventory(Long productId) {
        return inventoryGetService.hasAvailableInventory(productId);
    }
}
