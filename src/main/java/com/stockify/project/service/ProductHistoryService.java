package com.stockify.project.service;

import com.stockify.project.model.dto.ProductPriceHistoryDto;
import com.stockify.project.model.entity.ProductAuditEntity;
import com.stockify.project.repository.ProductAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.stockify.project.constant.CacheConstants.PRODUCT_AUDIT_DETAIL;

@Service
@RequiredArgsConstructor
public class ProductHistoryService {

    private final ProductAuditRepository productAuditRepository;

    @Cacheable(value = PRODUCT_AUDIT_DETAIL, key = "#productId")
    public List<ProductPriceHistoryDto> getPriceHistory(Long productId) {
        List<ProductAuditEntity> audits = productAuditRepository.findAll()
                .stream()
                .filter(productAudit -> productAudit.getId().equals(productId))
                .sorted(Comparator.comparing(ProductAuditEntity::getLastModifiedDate))
                .toList();
        List<ProductPriceHistoryDto> history = new ArrayList<>();
        for (int i = 0; i < audits.size(); i++) {
            ProductAuditEntity current = audits.get(i);
            ProductAuditEntity next = i + 1 < audits.size() ? audits.get(i + 1) : null;
            LocalDateTime from = current.getLastModifiedDate();
            LocalDateTime to = next != null ? next.getLastModifiedDate().minusDays(1) : LocalDateTime.now();
            String dateRange = from.toLocalDate() + " - " + to.toLocalDate();
            history.add(ProductPriceHistoryDto.builder()
                    .dateRange(dateRange)
                    .price(current.getPrice())
                    .build());
        }
        return history;
    }
}
