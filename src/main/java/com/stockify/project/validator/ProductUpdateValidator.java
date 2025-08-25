package com.stockify.project.validator;

import com.stockify.project.exception.ProductNameAlreadyUseException;
import com.stockify.project.model.entity.tenant.ProductEntity;
import com.stockify.project.repository.tenant.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

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
}
