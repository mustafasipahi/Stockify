package com.stockify.project.service;

import com.stockify.project.converter.InventoryConverter;
import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.stockify.project.constant.CacheConstants.*;
import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class InventoryDefaultService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = INVENTORY_ALL, allEntries = true),
            @CacheEvict(value = INVENTORY_AVAILABLE, allEntries = true),
            @CacheEvict(value = INVENTORY_CRITICAL, allEntries = true),
            @CacheEvict(value = INVENTORY_OUT_OF, allEntries = true)
    })
    public void saveDefault(Long productId) {
        InventoryEntity inventoryEntity = InventoryEntity.builder()
                .productId(productId)
                .price(BigDecimal.ZERO)
                .productCount(0)
                .criticalProductCount(0)
                .status(InventoryStatus.OUT_OF_INVENTORY)
                .tenantId(getTenantId())
                .build();
        inventoryRepository.save(inventoryEntity);
    }

}
