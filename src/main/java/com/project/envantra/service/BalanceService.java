package com.project.envantra.service;

import com.project.envantra.model.entity.TransactionEntity;
import com.project.envantra.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final TransactionRepository transactionRepository;

    public BigDecimal getBrokerCurrentBalance(Long brokerId) {
        return transactionRepository.findTopByBrokerIdOrderByCreatedDateDesc(brokerId)
                .map(TransactionEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Map<Long, BigDecimal> getBrokerCurrentBalanceMap(List<Long> brokerIds) {
        List<TransactionEntity> latestTransactions = transactionRepository.findLatestTransactionsByBrokerIds(brokerIds);
        return latestTransactions.stream()
                .collect(Collectors.toMap(TransactionEntity::getBrokerId, TransactionEntity::getBalance));
    }
}
