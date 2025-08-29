package com.stockify.project.service;

import com.stockify.project.converter.ProductConverter;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.exception.ProductIdException;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.model.request.ProductSearchRequest;
import com.stockify.project.model.request.ProductUpdateRequest;
import com.stockify.project.repository.ProductRepository;
import com.stockify.project.specification.ProductSpecification;
import com.stockify.project.util.InventoryCodeGenerator;
import com.stockify.project.validator.ProductCreateValidator;
import com.stockify.project.validator.ProductUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stockify.project.constant.CacheConstants.PRODUCT_DETAIL;
import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductCreateValidator createValidator;
    private final ProductUpdateValidator updateValidator;
    private final InventoryCodeGenerator inventoryCodeGenerator;
    private final ProductConverter productConverter;

    @Transactional
    public ProductDto save(ProductCreateRequest request) {
        createValidator.validate(request);
        ProductEntity product = ProductEntity.builder()
                .categoryId(request.getCategoryId())
                .inventoryCode(inventoryCodeGenerator.generateInventoryCode())
                .name(request.getName())
                .status(ProductStatus.ACTIVE)
                .tenantId(getTenantId())
                .build();
        ProductEntity savedProduct = productRepository.save(product);
        return productConverter.toIdDto(savedProduct);
    }

    @Transactional
    @CacheEvict(value = PRODUCT_DETAIL, key = "#request.productId")
    public ProductDto update(ProductUpdateRequest request) {
        if (request.getProductId() == null) {
            throw new ProductIdException();
        }
        ProductEntity productEntity = productRepository.findByIdAndTenantId(request.getProductId(), getTenantId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
        if (StringUtils.isNotBlank(request.getName())) {
            updateValidator.validateName(request.getName());
            productEntity.setName(request.getName());
        }
        ProductEntity updatedProduct = productRepository.save(productEntity);
        return productConverter.toIdDto(updatedProduct);
    }

    @Transactional
    @CacheEvict(value = PRODUCT_DETAIL, key = "#productId")
    public ProductDto delete(Long productId) {
        ProductEntity productEntity = productRepository.findByIdAndTenantId(productId, getTenantId())
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productEntity.setStatus(ProductStatus.PASSIVE);
        ProductEntity deletedProduct = productRepository.save(productEntity);
        return productConverter.toIdDto(deletedProduct);
    }

    @Cacheable(value = PRODUCT_DETAIL, key = "#productId")
    public ProductDto detail(Long productId) {
        return productRepository.findByIdAndTenantId(productId, getTenantId())
                .map(productConverter::toDto)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public List<ProductDto> getAll(ProductSearchRequest request) {
        Specification<ProductEntity> specification = ProductSpecification.filter(request);
        return productRepository.findAll(specification).stream()
                .map(productConverter::toDto)
                .toList();
    }
}
