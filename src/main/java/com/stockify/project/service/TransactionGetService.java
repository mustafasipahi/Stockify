package com.stockify.project.service;

import com.stockify.project.converter.TransactionConverter;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.model.request.TransactionSearchRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.repository.TransactionRepository;
import com.stockify.project.service.document.DocumentGetService;
import com.stockify.project.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    public Page<TransactionDto> getAllTransactions(TransactionSearchRequest request, int page, int size) {
        BrokerDto broker = getBroker(request.getBrokerId());
        Specification<TransactionEntity> specification = TransactionSpecification.filter(request);
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionEntity> transactions = transactionRepository.findAll(specification, pageable);
        Map<Long, DocumentResponse> documents = getTransactionDocuments(transactions);
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
