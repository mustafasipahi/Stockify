package com.stockify.project.service;

import com.stockify.project.converter.TransactionConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.enums.TransactionType;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.model.request.TransactionSearchRequest;
import com.stockify.project.repository.TransactionRepository;
import com.stockify.project.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.stockify.project.constant.CacheConstants.BROKER_BALANCE;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BrokerService brokerService;

    @Transactional
    @CacheEvict(value = BROKER_BALANCE, key = "#salesEntity.brokerId")
    public void createSalesTransaction(SalesEntity salesEntity) {
        BigDecimal currentBalance = getBrokerCurrentBalance(salesEntity.getBrokerId(), salesEntity.getTenantId());
        BigDecimal newBalance = currentBalance.add(salesEntity.getTotalPrice());
        TransactionEntity transaction = TransactionEntity.builder()
                .tenantId(salesEntity.getTenantId())
                .brokerId(salesEntity.getBrokerId())
                .type(TransactionType.SALE)
                .salesId(salesEntity.getId())
                .documentNumber(salesEntity.getDocumentNumber())
                .price(salesEntity.getTotalPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    @CacheEvict(value = BROKER_BALANCE, key = "#paymentEntity.brokerId")
    public void createPaymentTransaction(PaymentEntity paymentEntity) {
        BigDecimal currentBalance = getBrokerCurrentBalance(paymentEntity.getBrokerId(), paymentEntity.getTenantId());
        BigDecimal newBalance = currentBalance.subtract(paymentEntity.getPrice());
        TransactionEntity transaction = TransactionEntity.builder()
                .tenantId(paymentEntity.getTenantId())
                .brokerId(paymentEntity.getBrokerId())
                .type(TransactionType.PAYMENT)
                .paymentId(paymentEntity.getId())
                .documentNumber(paymentEntity.getDocumentNumber())
                .price(paymentEntity.getPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }

    @Cacheable(value = BROKER_BALANCE, key = "#brokerId")
    public BigDecimal getBrokerCurrentBalanceCache(Long brokerId, Long tenantId) {
        return getBrokerCurrentBalance(brokerId, tenantId);
    }

    public List<TransactionDto> getTransactions(TransactionSearchRequest request) {
        BrokerDto broker = getBroker(request.getBrokerId());
        Specification<TransactionEntity> specification = TransactionSpecification.filter(request);
        return transactionRepository.findAll(specification).stream()
                .map(transactionEntity -> TransactionConverter.toDto(transactionEntity, broker))
                .toList();
    }

    private BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return transactionRepository.findTopByBrokerIdAndTenantIdOrderByCreatedDateDesc(brokerId, tenantId)
                .map(TransactionEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    private BrokerDto getBroker(Long brokerId) {
        BrokerDto broker = brokerService.detail(brokerId);
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(brokerId);
        }
        return broker;
    }
}
