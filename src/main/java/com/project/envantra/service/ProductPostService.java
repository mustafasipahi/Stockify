package com.project.envantra.service;

import com.project.envantra.converter.ProductConverter;
import com.project.envantra.enums.ProductStatus;
import com.project.envantra.exception.HasAvailableInventoryException;
import com.project.envantra.exception.ProductIdException;
import com.project.envantra.exception.ProductNotFoundException;
import com.project.envantra.model.dto.ProductDto;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.model.request.ProductCreateRequest;
import com.project.envantra.model.request.ProductUpdateRequest;
import com.project.envantra.repository.ProductRepository;
import com.project.envantra.validator.ProductCreateValidator;
import com.project.envantra.validator.ProductUpdateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.project.envantra.util.LoginContext.getUsername;

@Slf4j
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
    private final ToActiveService toActiveService;

    @Transactional
    public ProductDto save(ProductCreateRequest request) {
        createValidator.validate(request);
        ProductEntity product = productConverter.toEntity(request);
        ProductEntity savedProduct = productRepository.save(product);
        inventoryPostService.saveDefault(savedProduct.getId());
        log.info("User {} saved product {}", getUsername(), savedProduct);
        return productConverter.toIdDto(savedProduct);
    }

    @Transactional
    public ProductDto update(ProductUpdateRequest request) {
        if (request.getProductId() == null) {
            throw new ProductIdException();
        }
        ProductEntity productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
        if (StringUtils.isNotBlank(request.getName()) && !request.getName().equals(productEntity.getName())) {
            updateValidator.validateName(productEntity.getId(), request.getName());
            productEntity.setName(request.getName());
        }
        if (request.getCategoryId() != null) {
            updateValidator.validateCategory(request.getCategoryId());
            productEntity.setCategoryId(request.getCategoryId());
        }
        ProductEntity updatedProduct = productRepository.save(productEntity);
        log.info("User {} updated product {}", getUsername(), updatedProduct);
        return productConverter.toIdDto(updatedProduct);
    }

    @Transactional
    public ProductDto delete(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        if (hasAvailableInventory(productId)) {
            throw new HasAvailableInventoryException(productId);
        }
        productEntity.setStatus(ProductStatus.PASSIVE);
        ProductEntity deletedProduct = productRepository.save(productEntity);
        toPassiveService.updateToPassiveByProductId(productId);
        log.info("User {} deleted product {}", getUsername(), deletedProduct);
        return productConverter.toIdDto(deletedProduct);
    }

    @Transactional
    public ProductDto activate(Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productEntity.setStatus(ProductStatus.ACTIVE);
        ProductEntity activatedProduct = productRepository.save(productEntity);
        toActiveService.updateToActiveByProductId(productId);
        log.info("User {} activated product {}", getUsername(), activatedProduct);
        return productConverter.toIdDto(activatedProduct);
    }

    private boolean hasAvailableInventory(Long productId) {
        return inventoryGetService.hasAvailableInventory(productId);
    }
}
