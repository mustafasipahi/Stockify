package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentResponse toResponse(PaymentCreateRequest request, BrokerDto broker) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(request.getPaymentPrice())
                .build();
    }
}
