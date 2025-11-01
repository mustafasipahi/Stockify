package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentDto toDto(PaymentCreateRequest request, BrokerDto broker) {
        return PaymentDto.builder()
                .broker(broker)
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .build();
    }

    public static PaymentEntity toEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                .brokerId(paymentDto.getBroker().getBrokerId())
                .price(paymentDto.getPrice())
                .type(paymentDto.getType())
                .tenantId(getTenantId())
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
