package com.stockify.project.converter;

import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.model.request.UserCreationEmailRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.Optional;

import static com.stockify.project.util.DateUtil.getTime;
import static com.stockify.project.util.TenantContext.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerConverter {

    public static BrokerEntity toEntity(BrokerCreateRequest request, Long brokerUserId) {
        return BrokerEntity.builder()
                .vkn(request.getVkn())
                .discountRate(Optional.ofNullable(request.getDiscountRate()).orElse(BigDecimal.ZERO))
                .status(BrokerStatus.ACTIVE)
                .brokerUserId(brokerUserId)
                .creatorUserId(getUserId())
                .tenantId(getTenantId())
                .build();
    }

    public static BrokerDto toDto(BrokerEntity brokerEntity, BigDecimal currentBalance) {
        UserEntity user = getUser();
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .brokerUserId(brokerEntity.getBrokerUserId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getRoleName())
                .vkn(brokerEntity.getVkn())
                .discountRate(brokerEntity.getDiscountRate())
                .currentBalance(currentBalance)
                .status(brokerEntity.getStatus())
                .createdDate(getTime(brokerEntity.getCreatedDate()))
                .lastModifiedDate(getTime(brokerEntity.getLastModifiedDate()))
                .build();
    }

    public static UserCreationEmailRequest toEmailRequest(String username, String password,
                                                          UserEntity creatorUser, UserEntity brokerUser) {
        return UserCreationEmailRequest.builder()
                .brokerUsername(username)
                .brokerPassword(password)
                .brokerFirstName(brokerUser.getFirstName())
                .brokerLastName(brokerUser.getLastName())
                .creatorUserFirstName(creatorUser.getFirstName())
                .creatorUserLastName(creatorUser.getLastName())
                .build();
    }

    public static BrokerDto toIdDto(BrokerEntity brokerEntity) {
        return BrokerDto.builder()
                .brokerId(brokerEntity.getId())
                .build();
    }
}
