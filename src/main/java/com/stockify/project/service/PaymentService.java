package com.stockify.project.service;

import com.stockify.project.converter.PaymentConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.repository.PaymentRepository;
import com.stockify.project.validator.PaymentCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentConverter paymentConverter;
    private final BrokerGetService brokerGetService;
    private final TransactionPostService transactionPostService;
    private final DocumentService documentService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        PaymentDto paymentDto = paymentConverter.toDto(request);
        PaymentEntity paymentEntity = paymentConverter.toEntity(paymentDto);
        String documentId = uploadDocument(paymentDto, paymentEntity);
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        saveTransaction(savedPaymentEntity);
        return paymentConverter.toResponse(savedPaymentEntity, broker, documentId);
    }

    private BrokerDto getBroker(Long brokerId) {
        BrokerDto broker = brokerGetService.detail(brokerId);
        if (broker.getStatus() != BrokerStatus.ACTIVE) {
            throw new BrokerNotFoundException(brokerId);
        }
        return broker;
    }

    private PaymentEntity savePaymentEntity(PaymentEntity paymentEntity) {
        return paymentRepository.save(paymentEntity);
    }

    private String uploadDocument(PaymentDto paymentDto, PaymentEntity paymentEntity) {
        DocumentResponse documentResponse = documentService.uploadPaymentFile(paymentDto);
        String documentId = documentResponse.getId();
        paymentEntity.setDocumentId(documentId);
        return documentId;
    }

    private void saveTransaction(PaymentEntity savedPaymentEntity) {
        transactionPostService.createPaymentTransaction(savedPaymentEntity);
    }
}
