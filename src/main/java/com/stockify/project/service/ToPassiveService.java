package com.stockify.project.service;

import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.repository.InventoryRepository;
import com.stockify.project.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@Service
@RequiredArgsConstructor
public class ToPassiveService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateToPassiveByCategoryId(Long categoryId) {
        List<ProductEntity> productList = productRepository.findByCategoryIdAndTenantId(categoryId, getTenantId()).stream()
                .peek(product -> product.setStatus(ProductStatus.PASSIVE))
                .toList();
        productRepository.saveAll(productList).forEach(product -> inventoryToPassive(product.getId()));
    }

    @Transactional
    public void updateToPassiveByProductId(Long productId) {
        inventoryToPassive(productId);
    }

    private void inventoryToPassive(Long productId) {
        Long userId = getUserId();
        Long tenantId = getTenantId();
        inventoryRepository.findByIdAndOwnerUserIdAndTenantId(productId, userId, tenantId)
                .ifPresent(inventory -> {
                    inventory.setActive(false);
                    inventoryRepository.save(inventory);
                });
    }
}
