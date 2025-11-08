package com.stockify.project.service;

import com.stockify.project.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class ToActiveService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateToActiveByProductId(Long productId) {
        inventoryToActive(productId);
    }

    private void inventoryToActive(Long productId) {
        Long tenantId = getTenantId();
        inventoryRepository.findByProductIdAndTenantId(productId, tenantId)
                .ifPresent(inventory -> {
                    inventory.setActive(true);
                    inventoryRepository.save(inventory);
                });
    }
}
