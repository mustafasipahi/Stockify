package com.stockify.project.service;

import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.stockify.project.constant.CacheConstants.BROKER_BALANCE;

@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TransactionRepository transactionRepository;

    @Cacheable(value = BROKER_BALANCE, key = "#brokerId")
    public BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return transactionRepository.findTopByBrokerIdAndTenantIdOrderByCreatedDateDesc(brokerId, tenantId)
                .map(TransactionEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}
