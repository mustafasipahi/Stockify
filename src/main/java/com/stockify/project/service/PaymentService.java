package com.stockify.project.service;

import com.stockify.project.converter.PaymentConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.repository.PaymentRepository;
import com.stockify.project.validator.PaymentCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.stockify.project.constant.DocumentNumberConstants.PAYMENT_DEFAULT;
import static com.stockify.project.constant.DocumentNumberConstants.PAYMENT_PREFIX;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BrokerService brokerService;
    private final TransactionService transactionService;
    private final DocumentService documentService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        PaymentEntity paymentEntity = PaymentConverter.toEntity(request);
        String documentId = uploadDocument(paymentEntity);
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        evictBrokerCache(savedPaymentEntity.getBrokerId());
        saveTransaction(savedPaymentEntity);
        return PaymentConverter.toResponse(savedPaymentEntity, broker, documentId);
    }

    private BrokerDto getBroker(Long brokerId) {
        BrokerDto broker = brokerService.detail(brokerId);
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(brokerId);
        }
        return broker;
    }

    private PaymentEntity savePaymentEntity(PaymentEntity paymentEntity) {
        return paymentRepository.save(paymentEntity);
    }

    private void evictBrokerCache(Long brokerId) {
        brokerService.evictBrokerCache(brokerId);
    }

    private String uploadDocument(PaymentEntity paymentEntity) {
        DocumentResponse documentResponse = documentService.uploadPaymentFile();
        String documentId = documentResponse.getId();
        paymentEntity.setDocumentId(documentId);
        paymentEntity.setDocumentNumber(getDocumentNumber());
        return documentId;
    }

    private void saveTransaction(PaymentEntity savedPaymentEntity) {
        transactionService.createPaymentTransaction(savedPaymentEntity);
    }

    private String getDocumentNumber() {
        return Optional.ofNullable(paymentRepository.findMaxDocumentNumberNumeric())
                .map(lastDocumentNumber -> PAYMENT_PREFIX + (lastDocumentNumber + 1))
                .orElse(PAYMENT_PREFIX + PAYMENT_DEFAULT);
    }
}
