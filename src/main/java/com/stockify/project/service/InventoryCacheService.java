package com.stockify.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import static com.stockify.project.constant.CacheConstants.*;
import static com.stockify.project.constant.CacheConstants.INVENTORY_OUT_OF;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryCacheService {

    @Caching(evict = {
            @CacheEvict(value = INVENTORY_ALL, allEntries = true),
            @CacheEvict(value = INVENTORY_AVAILABLE, allEntries = true),
            @CacheEvict(value = INVENTORY_CRITICAL, allEntries = true),
            @CacheEvict(value = INVENTORY_OUT_OF, allEntries = true)
    })
    public void evictAll() {
        log.info("Evicting cache of all products");
    }
}
