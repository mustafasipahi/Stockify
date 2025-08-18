package com.stockify.project.validator;

import com.stockify.project.exception.ProductAmountException;
import com.stockify.project.exception.ProductNameAlreadyUseException;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@AllArgsConstructor
public class ProductUpdateValidator {

    private final ProductRepository productRepository;

    public void validateName(String productName) {
        Optional<ProductEntity> product = productRepository.findByName(productName);
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }

    public void validateAmount(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductAmountException();
        }
    }
}
