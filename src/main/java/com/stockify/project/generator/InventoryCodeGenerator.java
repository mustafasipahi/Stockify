package com.stockify.project.generator;

import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.util.LoginContext.getUserId;

@Slf4j
@Component
@AllArgsConstructor
public class InventoryCodeGenerator {

    private static final String PREFIX = "KRY";
    private final ProductRepository productRepository;

    @Transactional
    public String generateInventoryCode() {
        Long userId = getUserId();
        String lastCode = productRepository.findFirstByCreatorUserIdOrderByCreatedDateDesc(userId)
                .map(ProductEntity::getInventoryCode)
                .orElse(null);
        int newSequence = 1;
        if (lastCode != null && lastCode.contains("-")) {
            try {
                String[] parts = lastCode.split("-");
                if (parts.length > 1) {
                    newSequence = Integer.parseInt(parts[1]) + 1;
                }
            } catch (NumberFormatException e) {
                log.error("generateInventoryCode", e);
            }
        }
        String sequence = String.format("%03d", newSequence);
        return PREFIX + "-" + sequence;
    }
}
