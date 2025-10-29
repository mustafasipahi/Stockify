package com.stockify.project.service;

import com.stockify.project.converter.ProductConverter;
import com.stockify.project.enums.ProductStatus;
import com.stockify.project.exception.ProductNotFoundException;
import com.stockify.project.model.dto.ProductDto;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductSearchRequest;
import com.stockify.project.repository.ProductRepository;
import com.stockify.project.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Service
@RequiredArgsConstructor
public class ProductGetService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    public ProductDto detail(Long productId) {
        return productRepository.findByIdAndTenantId(productId, getTenantId())
                .map(productConverter::toDto)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Map<Long, ProductDto> detailMap(List<Long> productIdList) {
        return productRepository.findAllByIdInAndTenantId(productIdList, getTenantId()).stream()
                .map(productConverter::toDto)
                .collect(Collectors.toMap(ProductDto::getProductId, Function.identity()));
    }

    public List<ProductDto> getAll(ProductSearchRequest request) {
        Specification<ProductEntity> specification = ProductSpecification.filter(request);
        return productRepository.findAll(specification).stream()
                .map(productConverter::toDto)
                .toList();
    }

    public boolean hasAvailableProducts(Long categoryId) {
        return productRepository.findByCreatorUserIdAndCategoryIdAndTenantIdAndStatus(getUserId(), categoryId, getTenantId(), ProductStatus.ACTIVE)
                .isPresent();
    }
}
