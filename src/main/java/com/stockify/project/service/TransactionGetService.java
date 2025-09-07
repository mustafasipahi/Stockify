package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
import com.stockify.project.converter.TransactionConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.entity.TransactionEntity;
import com.stockify.project.model.request.TransactionSearchRequest;
import com.stockify.project.model.response.DocumentResponse;
import com.stockify.project.repository.BrokerRepository;
import com.stockify.project.repository.TransactionRepository;
import com.stockify.project.specification.TransactionSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.stockify.project.util.TenantContext.getTenantId;

@Service
@RequiredArgsConstructor
public class TransactionGetService {

    private final TransactionRepository transactionRepository;
    private final BrokerRepository brokerRepository;
    private final DocumentGetService documentGetService;

    public Page<TransactionDto> getAllTransactions(TransactionSearchRequest request, int page, int size) {
        BrokerDto broker = getBroker(request.getBrokerId());
        Specification<TransactionEntity> specification = TransactionSpecification.filter(request);
        Sort sort = Sort.by("createdDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<TransactionEntity> transactions = transactionRepository.findAll(specification, pageable);
        Set<Long> documentIds = transactions.stream()
                .map(TransactionEntity::getDocumentId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, DocumentResponse> documents = documentGetService.getAllDocument(documentIds).stream()
                .collect(Collectors.toMap(DocumentResponse::getId, Function.identity()));
        return transactions.map(transaction -> {
            DocumentResponse doc = documents.get(transaction.getDocumentId());
            String downloadUrl = (doc != null) ? doc.getDownloadUrl() : null;
            return TransactionConverter.toDto(transaction, broker, downloadUrl);
        });
    }

    public BigDecimal getBrokerCurrentBalance(Long brokerId, Long tenantId) {
        return transactionRepository.findTopByBrokerIdAndTenantIdOrderByCreatedDateDesc(brokerId, tenantId)
                .map(TransactionEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    private BrokerDto getBroker(Long brokerId) {
        Long tenantId = getTenantId();
        return brokerRepository.findByIdAndTenantId(brokerId, tenantId)
                .filter(broker -> broker.getStatus() == BrokerStatus.ACTIVE)
                .map(brokerEntity -> BrokerConverter.toDto(brokerEntity, getBrokerCurrentBalance(brokerId, tenantId)))
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
    }
}
