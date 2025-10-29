package com.stockify.project.service;

import com.stockify.project.converter.PaymentConverter;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.CompanyInfoDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.repository.PaymentRepository;
import com.stockify.project.service.document.DocumentPostService;
import com.stockify.project.service.email.PaymentEmailService;
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
    private final DocumentPostService documentPostService;
    private final CompanyGetService companyGetService;
    private final PaymentEmailService paymentEmailService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        PaymentDto paymentDto = paymentConverter.toDto(request, broker);
        addCompanyInfo(paymentDto);
        PaymentEntity paymentEntity = paymentConverter.toEntity(paymentDto);
        DocumentResponse documentResponse = uploadDocument(paymentDto);
        paymentEntity.setDocumentId(documentResponse.getDocumentId());
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        saveTransaction(savedPaymentEntity);
        sendEmail(paymentDto, documentResponse);
        return paymentConverter.toResponse(savedPaymentEntity, broker, documentResponse.getDownloadUrl());
    }

    private BrokerDto getBroker(Long brokerId) {
        return brokerGetService.getActiveBroker(brokerId);
    }

    private void addCompanyInfo(PaymentDto paymentDto) {
        CompanyInfoDto companyInfo = companyGetService.getCompanyInfo();
        paymentDto.setCompanyInfo(companyInfo);
    }

    private DocumentResponse uploadDocument(PaymentDto paymentDto) {
        return documentPostService.uploadPaymentFile(paymentDto);
    }

    private PaymentEntity savePaymentEntity(PaymentEntity paymentEntity) {
        return paymentRepository.save(paymentEntity);
    }

    private void saveTransaction(PaymentEntity savedPaymentEntity) {
        transactionPostService.createPaymentTransaction(savedPaymentEntity);
    }

    private void sendEmail(PaymentDto paymentDto, DocumentResponse documentResponse) {
        paymentEmailService.sendPaymentNotifications(paymentDto, documentResponse);
    }
}
