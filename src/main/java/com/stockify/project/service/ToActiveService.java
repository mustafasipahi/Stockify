package com.stockify.project.service;

import com.stockify.project.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ToActiveService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateToActiveByProductId(Long productId) {
        inventoryToActive(productId);
    }

    private void inventoryToActive(Long productId) {
        inventoryRepository.findByProductId(productId)
                .ifPresent(inventory -> {
                    inventory.setActive(true);
                    inventoryRepository.save(inventory);
                });
    }
}
