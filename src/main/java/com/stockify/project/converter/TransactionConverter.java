package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.TransactionDto;
import com.stockify.project.model.entity.TransactionEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionConverter {

    public static TransactionDto toDto(TransactionEntity transactionEntity, BrokerDto broker) {
        return TransactionDto.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .price(transactionEntity.getPrice())
                .balance(transactionEntity.getBalance())
                .type(transactionEntity.getType())
                .createdDate(transactionEntity.getCreatedDate())
                .build();
    }
}
