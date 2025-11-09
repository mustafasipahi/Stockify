package com.project.envantra.converter;

import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.PaymentDto;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentCreateRequest;
import com.project.envantra.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentDto toDto(PaymentCreateRequest request, BrokerDto broker) {
        return PaymentDto.builder()
                .broker(broker)
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .createdDate(LocalDateTime.now())
                .build();
    }

    public static PaymentEntity toEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(paymentDto.getBroker().getBrokerId())
                .price(paymentDto.getPrice())
                .type(paymentDto.getType())
                .build();
    }

    public static PaymentResponse toResponse(PaymentEntity paymentEntity, BrokerDto broker, String downloadUrl) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(paymentEntity.getPrice())
                .downloadUrl(downloadUrl)
                .build();
    }
}
