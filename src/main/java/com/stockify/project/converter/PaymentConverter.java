package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentEntity toEntity(PaymentCreateRequest request, String documentNumber, Long tenantId) {
        return PaymentEntity.builder()
                .documentNumber(documentNumber)
                .brokerId(request.getBrokerId())
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .tenantId(tenantId)
                .build();
    }

    public static PaymentResponse toResponse(PaymentEntity paymentEntity, BrokerDto broker) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(paymentEntity.getPrice())
                .documentNumber(paymentEntity.getDocumentNumber())
                .build();
    }
}
