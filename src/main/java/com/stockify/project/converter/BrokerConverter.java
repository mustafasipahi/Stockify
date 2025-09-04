package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

import static com.stockify.project.util.DateUtil.getTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerConverter {

    public static BrokerDto toDto(BrokerEntity brokerEntity, BigDecimal currentBalance) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .firstName(brokerEntity.getFirstName())
                .lastName(brokerEntity.getLastName())
                .discountRate(brokerEntity.getDiscountRate())
                .currentBalance(currentBalance)
                .status(brokerEntity.getStatus())
                .createdDate(getTime(brokerEntity.getCreatedDate()))
                .lastModifiedDate(getTime(brokerEntity.getLastModifiedDate()))
                .build();
    }

    public static BrokerDto toIdDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .build();
    }
}
