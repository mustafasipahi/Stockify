package com.stockify.project.util;

import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class StockCodeGenerator {

    private static final String PREFIX = "KRY-";
    private final ProductRepository productRepository;

    @Transactional
    public String generateStockCode() {
        String lastCode = productRepository.findFirstByOrderByCreatedDateDesc()
                .map(ProductEntity::getStockCode)
                .orElse(null);
        int newSequence = 1;
        if (lastCode != null) {
            String[] parts = lastCode.split("-");
            newSequence = Integer.parseInt(parts[1]) + 1;
        }
        String sequence = String.format("%04d", newSequence);
        return PREFIX + sequence;
    }
}
