package com.project.envantra.service;

import com.project.envantra.controller.PaymentSearchRequest;
import com.project.envantra.converter.PaymentConverter;
import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.exception.PaymentNotFoundException;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.CompanyDto;
import com.project.envantra.model.dto.PaymentDto;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentCancelRequest;
import com.project.envantra.model.request.PaymentCreateRequest;
import com.project.envantra.model.request.PaymentUpdateRequest;
import com.project.envantra.model.response.DocumentResponse;
import com.project.envantra.model.response.PaymentResponse;
import com.project.envantra.repository.PaymentRepository;
import com.project.envantra.service.document.DocumentGetService;
import com.project.envantra.service.document.DocumentPostService;
import com.project.envantra.service.email.PaymentEmailService;
import com.project.envantra.specification.PaymentSpecification;
import com.project.envantra.validator.PaymentCancelValidator;
import com.project.envantra.validator.PaymentCreateValidator;
import com.project.envantra.validator.PaymentUpdateValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final BrokerGetService brokerGetService;
    private final TransactionPostService transactionPostService;
    private final DocumentPostService documentPostService;
    private final CompanyGetService companyGetService;
    private final PaymentEmailService paymentEmailService;
    private final DocumentGetService documentGetService;

    @Transactional
    public PaymentResponse save(PaymentCreateRequest request) {
        PaymentCreateValidator.validate(request);
        BrokerDto broker = getBroker(request.getBrokerId());
        PaymentDto paymentDto = PaymentConverter.toDto(request, broker);
        addCompany(paymentDto);
        PaymentEntity paymentEntity = PaymentConverter.toEntity(paymentDto);
        paymentEntity.setStatus(PaymentStatus.ACTIVE);
        DocumentResponse documentResponse = uploadDocument(paymentDto);
        paymentEntity.setDocumentId(documentResponse.getDocumentId());
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        saveTransaction(savedPaymentEntity);
        sendCreatedEmail(paymentDto, documentResponse);
        return PaymentConverter.toResponse(savedPaymentEntity, broker, documentResponse.getDownloadUrl());
    }

    @Transactional
    public PaymentResponse update(PaymentUpdateRequest request) {
        PaymentEntity existingPayment = getPaymentById(request.getPaymentId());
        PaymentUpdateValidator.validate(request, existingPayment);
        existingPayment.setStatus(PaymentStatus.UPDATED);
        savePaymentEntity(existingPayment);
        BrokerDto broker = getBroker(existingPayment.getBrokerId());
        PaymentDto paymentDto = PaymentConverter.toDto(request, broker, existingPayment.getId());
        addCompany(paymentDto);
        PaymentEntity paymentEntity = PaymentConverter.toEntity(paymentDto);
        paymentEntity.setStatus(PaymentStatus.ACTIVE);
        DocumentResponse documentResponse = uploadDocument(paymentDto);
        paymentEntity.setDocumentId(documentResponse.getDocumentId());
        PaymentEntity savedPaymentEntity = savePaymentEntity(paymentEntity);
        updateTransaction(savedPaymentEntity);
        sendUpdatedEmail(paymentDto, existingPayment, documentResponse);
        return PaymentConverter.toResponse(savedPaymentEntity, broker, documentResponse.getDownloadUrl());
    }

    @Transactional
    public PaymentResponse cancel(PaymentCancelRequest request) {
        PaymentEntity existingPayment = getPaymentById(request.getPaymentId());
        PaymentCancelValidator.validate(request, existingPayment);
        existingPayment.setStatus(PaymentStatus.CANCELLED);
        existingPayment.setCancelReason(request.getCancelReason());
        PaymentEntity savedPaymentEntity = savePaymentEntity(existingPayment);
        BrokerDto broker = getBroker(existingPayment.getBrokerId());
        PaymentDto paymentDto = PaymentConverter.toDto(savedPaymentEntity, broker);
        cancelTransaction(savedPaymentEntity);
        sendCancelledEmail(paymentDto);
        return PaymentConverter.toResponse(savedPaymentEntity, broker, null);
    }

    public Page<PaymentResponse> search(PaymentSearchRequest request, Pageable pageable) {
        Specification<PaymentEntity> specification = PaymentSpecification.filter(request);
        Page<PaymentEntity> allPayment = paymentRepository.findAll(specification, pageable);
        List<Long> brokerIds = allPayment.getContent().stream().map(PaymentEntity::getBrokerId).toList();
        Map<Long, BrokerDto> brokerMap = brokerGetService.getBrokerMap(brokerIds);
        Map<Long, DocumentResponse> documents = getPaymentDocuments(allPayment);
        return allPayment.map(paymentEntity -> {
            BrokerDto broker = brokerMap.getOrDefault(paymentEntity.getBrokerId(), new BrokerDto());
            DocumentResponse document = documents.getOrDefault(paymentEntity.getDocumentId(), new DocumentResponse());
            return PaymentConverter.toResponse(paymentEntity, broker, document.getDownloadUrl());
        });
    }

    public List<PaymentEntity> findAllByBrokerIdByDate(Collection<Long> brokerIds, LocalDateTime startDate, LocalDateTime endDate) {
        return paymentRepository.findAllByBrokerIdInAndCreatedDateBetween(brokerIds, startDate, endDate);
    }

    private PaymentEntity getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
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

    private void updateTransaction(PaymentEntity savedPaymentEntity) {
        transactionPostService.updatePaymentTransaction(savedPaymentEntity);
    }

    private void cancelTransaction(PaymentEntity savedPaymentEntity) {
        transactionPostService.cancelPaymentTransaction(savedPaymentEntity);
    }

    private void sendCreatedEmail(PaymentDto paymentDto, DocumentResponse documentResponse) {
        paymentEmailService.sendPaymentCreatedNotifications(paymentDto, documentResponse);
    }

    private void sendUpdatedEmail(PaymentDto newPaymentDto, PaymentEntity oldPayment, DocumentResponse newDocumentResponse) {
        paymentEmailService.sendPaymentUpdatedNotifications(newPaymentDto, newDocumentResponse, oldPayment);
    }

    private void sendCancelledEmail(PaymentDto paymentDto) {
        paymentEmailService.sendPaymentCancelledNotifications(paymentDto);
    }

    private Map<Long, DocumentResponse> getPaymentDocuments(Page<PaymentEntity> allPayment) {
        Set<Long> documentIds = new HashSet<>();
        allPayment.getContent().forEach(paymentEntity -> {
            if (paymentEntity.getDocumentId() != null) {
                documentIds.add(paymentEntity.getDocumentId());
            }
        });
        return documentGetService.getAllDocument(documentIds).stream()
                .collect(Collectors.toMap(DocumentResponse::getDocumentId, Function.identity()));
    }
}