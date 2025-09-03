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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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
                .documentId(salesEntity.getDocumentId())
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
                .documentId(paymentEntity.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentId(paymentEntity.getId())
                .documentNumber(paymentEntity.getDocumentNumber())
                .price(paymentEntity.getPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }

    public Page<TransactionDto> getAllTransactions(TransactionSearchRequest request, int page, int size) {
        BrokerDto broker = getBroker(request.getBrokerId());
        Specification<TransactionEntity> specification = TransactionSpecification.filter(request);
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return transactionRepository.findAll(specification, pageable)
                .map(transactionEntity -> TransactionConverter.toDto(transactionEntity, broker));
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
