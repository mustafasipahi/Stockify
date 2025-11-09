package com.project.envantra.service;

import com.project.envantra.enums.ProductStatus;
import com.project.envantra.model.entity.ProductEntity;
import com.project.envantra.repository.InventoryRepository;
import com.project.envantra.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class ToPassiveService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    @Transactional
    public void updateToPassiveByCategoryId(Long categoryId) {
        Long userId = getUserId();
        List<ProductEntity> productList = productRepository.findByCreatorUserIdAndCategoryId(userId, categoryId).stream()
                .peek(product -> product.setStatus(ProductStatus.PASSIVE))
                .toList();
        productRepository.saveAll(productList).forEach(product -> inventoryToPassive(product.getId()));
    }

    @Transactional
    public void updateToPassiveByProductId(Long productId) {
        inventoryToPassive(productId);
    }

    private void inventoryToPassive(Long productId) {
        inventoryRepository.findByProductId(productId)
                .ifPresent(inventory -> {
                    inventory.setActive(false);
                    inventoryRepository.save(inventory);
                });
    }
}
