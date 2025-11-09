package com.project.envantra.service;

import com.project.envantra.enums.TransactionType;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.entity.SalesEntity;
import com.project.envantra.model.entity.TransactionEntity;
import com.project.envantra.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class TransactionPostService {

    private final TransactionRepository transactionRepository;
    private final BalanceService balanceService;

    @Transactional
    public void createSalesTransaction(SalesEntity salesEntity, boolean createInvoice) {
        BigDecimal currentBalance = balanceService.getBrokerCurrentBalance(salesEntity.getBrokerId());
        BigDecimal newBalance = currentBalance.add(salesEntity.getTotalPrice());
        TransactionEntity transaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(salesEntity.getBrokerId())
                .documentId(salesEntity.getDocumentId())
                .invoiceId(salesEntity.getInvoiceId())
                .type(TransactionType.SALE)
                .salesId(salesEntity.getId())
                .requestedInvoice(createInvoice)
                .price(salesEntity.getTotalPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }

    @Transactional
    public void createPaymentTransaction(PaymentEntity paymentEntity) {
        BigDecimal currentBalance = balanceService.getBrokerCurrentBalance(paymentEntity.getBrokerId());
        BigDecimal newBalance = currentBalance.subtract(paymentEntity.getPrice());
        TransactionEntity transaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(paymentEntity.getBrokerId())
                .documentId(paymentEntity.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentType(paymentEntity.getType())
                .paymentId(paymentEntity.getId())
                .price(paymentEntity.getPrice())
                .balance(newBalance)
                .build();
        transactionRepository.save(transaction);
    }
}
