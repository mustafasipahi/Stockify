package com.project.envantra.service;

import com.project.envantra.enums.TransactionStatus;
import com.project.envantra.enums.TransactionType;
import com.project.envantra.exception.TransactionNotFoundException;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.entity.SalesEntity;
import com.project.envantra.model.entity.TransactionEntity;
import com.project.envantra.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static com.project.envantra.util.LoginContext.getUserId;

@Service
@RequiredArgsConstructor
public class TransactionPostService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    @Transactional
    public void createSalesTransaction(SalesEntity salesEntity, boolean createInvoice) {
        BigDecimal currentBalance = accountService.getBalance(salesEntity.getBrokerId());
        BigDecimal newBalance = currentBalance.add(salesEntity.getTotalPrice());
        String groupId = UUID.randomUUID().toString();

        TransactionEntity transaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(salesEntity.getBrokerId())
                .documentId(salesEntity.getDocumentId())
                .invoiceId(salesEntity.getInvoiceId())
                .type(TransactionType.SALE)
                .salesId(salesEntity.getId())
                .groupId(groupId)
                .requestedInvoice(createInvoice)
                .price(salesEntity.getTotalPrice())
                .balance(newBalance)
                .status(TransactionStatus.CREATED)
                .build();
        transactionRepository.save(transaction);
        accountService.updateToBalance(salesEntity.getBrokerId(), newBalance);
    }

    @Transactional
    public void createPaymentTransaction(PaymentEntity payment) {
        BigDecimal currentBalance = accountService.getBalance(payment.getBrokerId());
        BigDecimal newBalance = currentBalance.subtract(payment.getPrice());
        String groupId = UUID.randomUUID().toString();

        TransactionEntity transaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(payment.getBrokerId())
                .documentId(payment.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentId(payment.getId())
                .groupId(groupId)
                .price(payment.getPrice().negate())
                .balance(newBalance)
                .status(TransactionStatus.CREATED)
                .build();
        transactionRepository.save(transaction);
        accountService.updateToBalance(payment.getBrokerId(), newBalance);
    }

    @Transactional
    public void updatePaymentTransaction(PaymentEntity payment) {
        TransactionEntity oldTransaction = transactionRepository
                .findByPaymentId(payment.getOriginalPaymentId())
                .orElseThrow(TransactionNotFoundException::new);
        oldTransaction.setStatus(TransactionStatus.UPDATED);

        BigDecimal currentBalance = accountService.getBalance(payment.getBrokerId());
        BigDecimal balanceAfterCancel = currentBalance.subtract(oldTransaction.getPrice());
        BigDecimal newBalance = balanceAfterCancel.subtract(payment.getPrice());
        String groupId = oldTransaction.getGroupId();

        TransactionEntity reversedTransaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(oldTransaction.getBrokerId())
                .documentId(oldTransaction.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentId(oldTransaction.getPaymentId())
                .groupId(groupId)
                .price(oldTransaction.getPrice().negate())
                .balance(balanceAfterCancel)
                .status(TransactionStatus.UPDATED)
                .build();

        TransactionEntity transaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(payment.getBrokerId())
                .documentId(payment.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentId(payment.getId())
                .groupId(groupId)
                .price(payment.getPrice().negate())
                .balance(newBalance)
                .status(TransactionStatus.CREATED)
                .build();

        transactionRepository.saveAll(List.of(oldTransaction, reversedTransaction, transaction));
        accountService.updateToBalance(payment.getBrokerId(), newBalance);
    }

    @Transactional
    public void cancelPaymentTransaction(PaymentEntity payment) {
        TransactionEntity oldTransaction = transactionRepository
                .findByPaymentId(payment.getId())
                .orElseThrow(TransactionNotFoundException::new);
        oldTransaction.setStatus(TransactionStatus.CANCELLED);

        BigDecimal currentBalance = accountService.getBalance(payment.getBrokerId());
        BigDecimal newBalance = currentBalance.subtract(oldTransaction.getPrice());
        String groupId = oldTransaction.getGroupId();

        TransactionEntity reversedTransaction = TransactionEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(oldTransaction.getBrokerId())
                .documentId(oldTransaction.getDocumentId())
                .type(TransactionType.PAYMENT)
                .paymentId(oldTransaction.getPaymentId())
                .groupId(groupId)
                .price(oldTransaction.getPrice().negate())
                .balance(newBalance)
                .status(TransactionStatus.CANCELLED)
                .build();

        transactionRepository.saveAll(List.of(oldTransaction, reversedTransaction));
        accountService.updateToBalance(payment.getBrokerId(), newBalance);
    }
}