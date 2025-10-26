package com.stockify.project.service;

import com.stockify.project.enums.TransactionType;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.entity.SalesEntity;
import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransactionPostService {

    private final TransactionRepository transactionRepository;
    private final TransactionGetService transactionGetService;

    @Transactional
    public void createSalesTransaction(SalesEntity salesEntity) {
        BigDecimal currentBalance = transactionGetService.getBrokerCurrentBalance(salesEntity.getBrokerId(), salesEntity.getTenantId());
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
    public void createPaymentTransaction(PaymentEntity paymentEntity) {
        BigDecimal currentBalance = transactionGetService.getBrokerCurrentBalance(paymentEntity.getBrokerId(), paymentEntity.getTenantId());
        BigDecimal newBalance = currentBalance.subtract(paymentEntity.getPrice());
        TransactionEntity transaction = TransactionEntity.builder()
                .tenantId(paymentEntity.getTenantId())
                .brokerId(paymentEntity.getBrokerId())
                .documentId(paymentEntity.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentType(paymentEntity.getType())
                .paymentId(paymentEntity.getId())
                .documentNumber(paymentEntity.getDocumentNumber())
                .price(paymentEntity.getPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }
}
