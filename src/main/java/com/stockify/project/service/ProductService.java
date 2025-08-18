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
import com.stockify.project.util.StockCodeGenerator;
import com.stockify.project.validator.ProductCreateValidator;
import com.stockify.project.validator.ProductUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.constant.CacheConstants.PRODUCT_DETAIL;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductCreateValidator createValidator;
    private final ProductUpdateValidator updateValidator;
    private final ProductRepository productRepository;
    private final StockCodeGenerator stockCodeGenerator;

    @Transactional
    public ProductDto save(Long userId, ProductCreateRequest request) {
        createValidator.validate(request);
        ProductEntity product = ProductEntity.builder()
                .stockCode(stockCodeGenerator.generateStockCode())
                .name(request.getName())
                .amount(request.getAmount())
                .status(ProductStatus.ACTIVE)
                .createdAgentId(userId)
                .build();
        return ProductConverter.toIdDto(productRepository.save(product));
    }

    @Transactional
    @CacheEvict(value = PRODUCT_DETAIL, key = "#request.productId")
    public ProductDto update(Long userId, ProductUpdateRequest request) {
        if (request.getProductId() == null) {
            throw new ProductIdException();
        }
        ProductEntity productEntity = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));
        if (StringUtils.isNotBlank(request.getName())) {
            updateValidator.validateName(request.getName());
            productEntity.setName(request.getName());
        }
        if (request.getAmount() != null) {
            updateValidator.validateAmount(request.getAmount());
            productEntity.setAmount(request.getAmount());
        }
        productEntity.setUpdatedAgentId(userId);
        return ProductConverter.toIdDto(productRepository.save(productEntity));
    }

    @Transactional
    @CacheEvict(value = PRODUCT_DETAIL, key = "#productId")
    public ProductDto delete(Long userId, Long productId) {
        ProductEntity productEntity = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));
        productEntity.setStatus(ProductStatus.PASSIVE);
        productEntity.setUpdatedAgentId(userId);
        return ProductConverter.toIdDto(productRepository.save(productEntity));
    }

    @Cacheable(value = PRODUCT_DETAIL, key = "#productId")
    public ProductDto detail(Long productId) {
        return productRepository.findById(productId)
                .map(ProductConverter::toDto)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Page<ProductDto> search(ProductSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getSize(), Sort.by("createdDate").descending());
        Specification<ProductEntity> specification = ProductSpecification.searchByText(request);
        return productRepository.findAll(specification, pageRequest).map(ProductConverter::toDto);
    }
}
