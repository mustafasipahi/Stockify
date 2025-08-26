package com.stockify.project.util;

import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class InventoryCodeGenerator {

    private static final String PREFIX = "KRY-";
    private final ProductRepository productRepository;

    @Transactional
    public String generateInventoryCode() {
        String lastCode = productRepository.findFirstByTenantIdOrderByCreatedDateDesc(getTenantId())
                .map(ProductEntity::getInventoryCode)
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
