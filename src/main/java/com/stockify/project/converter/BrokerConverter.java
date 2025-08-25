package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerConverter {

    public static BrokerDto toDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .firstName(brokerEntity.getFirstName())
                .lastName(brokerEntity.getLastName())
                .discount(brokerEntity.getDiscount())
                .debtPrice(null)
                .createdDate(brokerEntity.getCreatedDate())
                .lastModifiedDate(brokerEntity.getLastModifiedDate())
                .build();
    }

    public static BrokerDto toIdDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .build();
    }
}
