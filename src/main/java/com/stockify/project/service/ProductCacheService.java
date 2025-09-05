package com.stockify.project.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import static com.stockify.project.constant.CacheConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductCacheService {

    @CacheEvict(value = PRODUCT_DETAIL, key = "#productId")
    public void evictDetail(Long productId) {
        log.info("Evicting cache productId: {}", productId);
    }
}
