package com.project.envantra.converter;

import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.TransactionDto;
import com.project.envantra.model.entity.TransactionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.project.envantra.util.DateUtil.getTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionConverter {

    public static TransactionDto toDto(TransactionEntity transactionEntity, BrokerDto broker,
                                       String downloadDocumentUrl, String downloadInvoiceUrl) {
        return TransactionDto.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .price(transactionEntity.getPrice())
                .balance(transactionEntity.getBalance())
                .type(transactionEntity.getType())
                .paymentType(transactionEntity.getPaymentType())
                .requestedInvoice(transactionEntity.isRequestedInvoice())
                .downloadDocumentUrl(downloadDocumentUrl)
                .downloadInvoiceUrl(downloadInvoiceUrl)
                .createdDate(getTime(transactionEntity.getCreatedDate()))
                .build();
    }
}
