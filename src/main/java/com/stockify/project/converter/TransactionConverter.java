package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.entity.TransactionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.DocumentUtil.getDownloadUrl;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionConverter {

    public static TransactionDto toDto(TransactionEntity transactionEntity, BrokerDto broker) {
        return TransactionDto.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .price(transactionEntity.getPrice())
                .balance(transactionEntity.getBalance())
                .type(transactionEntity.getType())
                .downloadUrl(getDownloadUrl(transactionEntity.getDocumentId()))
                .createdDate(getTime(transactionEntity.getCreatedDate()))
                .build();
    }
}
