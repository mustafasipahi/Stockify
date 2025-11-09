package com.project.envantra.service;

import com.project.envantra.converter.ProductConverter;
import com.project.envantra.enums.ProductStatus;
import com.project.envantra.exception.ProductNotFoundException;
import com.project.envantra.model.dto.ProductDto;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.model.request.ProductSearchRequest;
import com.project.envantra.repository.ProductRepository;
import com.project.envantra.specification.ProductSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class ProductGetService {

    private final ProductRepository productRepository;
    private final ProductConverter productConverter;

    public ProductDto detail(Long productId) {
        return productRepository.findById(productId)
                .map(productConverter::toDto)
                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Map<Long, ProductDto> detailMap(List<Long> productIdList) {
        return productRepository.findAllByIdIn(productIdList).stream()
                .map(productConverter::toDto)
                .collect(Collectors.toMap(ProductDto::getProductId, Function.identity()));
    }

    public List<ProductDto> getAll(ProductSearchRequest request) {
        Specification<ProductEntity> specification = ProductSpecification.filter(request);
        return productRepository.findAll(specification).stream()
                .map(productConverter::toDto)
                .toList();
    }

    public List<ProductDto> getAllPassive() {
        Specification<ProductEntity> specification = ProductSpecification.filterAllPassive();
        return productRepository.findAll(specification).stream()
                .map(productConverter::toDto)
                .toList();
    }

    public boolean hasAvailableProducts(Long categoryId) {
        return productRepository.findByCreatorUserIdAndCategoryIdAndStatus(getUserId(), categoryId, ProductStatus.ACTIVE)
                .isPresent();
    }
}
