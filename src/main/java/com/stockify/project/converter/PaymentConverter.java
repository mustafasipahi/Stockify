package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.stockify.project.util.LoginContext.getUserId;

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
