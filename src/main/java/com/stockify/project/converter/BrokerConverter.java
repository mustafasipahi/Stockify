package com.stockify.project.converter;

import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Optional;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.LoginContext.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerConverter {

    public static BrokerEntity toEntity(BrokerCreateRequest request, Long brokerUserId) {
        return BrokerEntity.builder()
                .discountRate(Optional.ofNullable(request.getDiscountRate()).orElse(BigDecimal.ZERO))
                .status(BrokerStatus.ACTIVE)
                .brokerUserId(brokerUserId)
                .creatorUserId(getUserId())
                .targetDayOfWeek(request.getTargetDayOfWeek())
                .build();
    }

    public static BrokerDto toDto(BrokerEntity broker, UserEntity brokerUser, BigDecimal currentBalance) {
        return BrokerDto.builder()
                .brokerId(broker.getId())
                .brokerUserId(broker.getBrokerUserId())
                .firstName(brokerUser.getFirstName())
                .lastName(brokerUser.getLastName())
                .email(brokerUser.getEmail())
                .discountRate(broker.getDiscountRate())
                .currentBalance(Optional.ofNullable(currentBalance).orElse(BigDecimal.ZERO))
                .status(broker.getStatus())
                .targetDayOfWeek(broker.getTargetDayOfWeek())
                .createdDate(getTime(broker.getCreatedDate()))
                .lastModifiedDate(getTime(broker.getLastModifiedDate()))
                .build();
    }

    public static BrokerDto toIdDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .build();
    }
}
