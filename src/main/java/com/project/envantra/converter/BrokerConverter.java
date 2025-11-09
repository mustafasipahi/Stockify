package com.project.envantra.converter;

import com.project.envantra.enums.BrokerStatus;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.entity.BrokerEntity;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.BrokerCreateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Optional;

import static com.project.envantra.util.DateUtil.getTime;
import static com.project.envantra.util.LoginContext.*;

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
