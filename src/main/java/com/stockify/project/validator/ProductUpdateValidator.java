package com.stockify.project.validator;

import com.stockify.project.exception.ProductNameAlreadyUseException;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class ProductUpdateValidator {

    private final ProductRepository productRepository;

    public void validateName(String productName) {
        Optional<ProductEntity> product = productRepository.findByNameAndTenantId(productName, getTenantId());
        if (product.isPresent()) {
            throw new ProductNameAlreadyUseException();
        }
    }
}
