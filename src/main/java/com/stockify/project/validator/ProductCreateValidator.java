package com.stockify.project.validator;

import com.stockify.project.exception.ProductAmountException;
import com.stockify.project.exception.ProductNameAlreadyUseException;
import com.stockify.project.exception.ProductNameException;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductCreateRequest;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductCreateValidator {

    private final ProductRepository productRepository;

    public void validate(ProductCreateRequest request) {
        validateName(request.getName());
        if (StringUtils.isBlank(request.getName())) {
            throw new ProductNameException();
        }
        if (request.getAmount() == null) {
            throw new ProductAmountException();
        }
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductAmountException();
        }
    }

    private void validateName(String productName) {
        Optional<ProductEntity> product = productRepository.findByName(productName);
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }
}
