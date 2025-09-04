package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentEntity toEntity(PaymentCreateRequest request) {
        return PaymentEntity.builder()
                .brokerId(request.getBrokerId())
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .tenantId(getTenantId())
                .build();
    }

    public static PaymentResponse toResponse(PaymentEntity paymentEntity, BrokerDto broker, String documentId) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(paymentEntity.getPrice())
                .downloadUrl(getDownloadUrl(documentId))
                .build();
    }
}
