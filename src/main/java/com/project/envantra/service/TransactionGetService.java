package com.project.envantra.service;

import com.project.envantra.converter.TransactionConverter;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.TransactionDto;
import com.project.envantra.model.entity.TransactionEntity;
import com.project.envantra.model.request.TransactionSearchRequest;
import com.project.envantra.model.response.DocumentResponse;
import com.project.envantra.repository.TransactionRepository;
import com.project.envantra.service.document.DocumentGetService;
import com.project.envantra.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionGetService {

    private final TransactionRepository transactionRepository;
    private final BrokerGetService brokerGetService;
    private final DocumentGetService documentGetService;

    public Page<TransactionDto> getAllTransactions(TransactionSearchRequest request, Pageable pageable) {
        Specification<TransactionEntity> specification = TransactionSpecification.filter(request);
        Page<TransactionEntity> transactions = transactionRepository.findAll(specification, pageable);
        Map<Long, DocumentResponse> documents = getTransactionDocuments(transactions);
        BrokerDto broker = getBroker(request.getBrokerId());
        return transactions.map(transaction -> {
            DocumentResponse documentResponse = documents.getOrDefault(transaction.getDocumentId(), null);
            String documentDownloadUrl = (documentResponse != null) ? documentResponse.getDownloadUrl() : null;
            DocumentResponse invoiceResponse = documents.getOrDefault(transaction.getInvoiceId(), null);
            String invoiceDownloadUrl = (invoiceResponse != null) ? invoiceResponse.getDownloadUrl() : null;
            return TransactionConverter.toDto(transaction, broker, documentDownloadUrl, invoiceDownloadUrl);
        });
    }

    private BrokerDto getBroker(Long brokerId) {
        return brokerGetService.getActiveBroker(brokerId);
    }

    private Map<Long, DocumentResponse> getTransactionDocuments(Page<TransactionEntity> transactions) {
        Set<Long> documentIds = new HashSet<>();
        transactions.getContent().forEach(transaction -> {
            if (transaction.getDocumentId() != null) {
                documentIds.add(transaction.getDocumentId());
            }
            if (transaction.getInvoiceId() != null) {
                documentIds.add(transaction.getInvoiceId());
            }
        });
        return documentGetService.getAllDocument(documentIds).stream()
                .collect(Collectors.toMap(DocumentResponse::getDocumentId, Function.identity()));
    }
}
