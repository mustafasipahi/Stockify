package com.stockify.project.service;

import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.stockify.project.util.TenantContext.getTenantId;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TransactionRepository transactionRepository;

    public BigDecimal getBrokerCurrentBalance(Long brokerId) {
        return transactionRepository.findTopByBrokerIdAndTenantIdOrderByCreatedDateDesc(brokerId, getTenantId())
                .map(TransactionEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Map<Long, BigDecimal> getBrokerCurrentBalanceMap(List<Long> brokerIds) {
        List<TransactionEntity> latestTransactions = transactionRepository.findLatestTransactionsByBrokerIds(brokerIds, getTenantId());
        return latestTransactions.stream()
                .collect(Collectors.toMap(TransactionEntity::getBrokerId, TransactionEntity::getBalance));
    }
}
