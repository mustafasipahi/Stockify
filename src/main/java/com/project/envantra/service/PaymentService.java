package com.project.envantra.service;

import com.project.envantra.converter.PaymentConverter;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.dto.PaymentDto;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentCreateRequest;
import com.project.envantra.model.response.DocumentResponse;
import com.project.envantra.model.response.PaymentResponse;
import com.project.envantra.repository.PaymentRepository;
import com.project.envantra.service.document.DocumentPostService;
import com.project.envantra.service.email.PaymentEmailService;
import com.project.envantra.validator.PaymentCreateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BrokerGetService brokerGetService;
    private final TransactionPostService transactionPostService;
    private final DocumentPostService documentPostService;
    private final CompanyGetService companyGetService;
    private final PaymentEmailService paymentEmailService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        PaymentDto paymentDto = PaymentConverter.toDto(request, broker);
        addCompany(paymentDto);
        PaymentEntity paymentEntity = PaymentConverter.toEntity(paymentDto);
        DocumentResponse documentResponse = uploadDocument(paymentDto);
        paymentEntity.setDocumentId(documentResponse.getDocumentId());
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        saveTransaction(savedPaymentEntity);
        sendEmail(paymentDto, documentResponse);
        return PaymentConverter.toResponse(savedPaymentEntity, broker, documentResponse.getDownloadUrl());
    }

    private BrokerDto getBroker(Long brokerId) {
        return brokerGetService.getActiveBroker(brokerId);
    }

    private void addCompany(PaymentDto paymentDto) {
        CompanyDto company = companyGetService.getCompanyDetail();
        paymentDto.setCompany(company);
    }

    private DocumentResponse uploadDocument(PaymentDto paymentDto) {
        DocumentResponse documentResponse = documentPostService.uploadPaymentPdf(paymentDto);
        paymentDto.setDocumentId(documentResponse.getDocumentId());
        return documentResponse;
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
