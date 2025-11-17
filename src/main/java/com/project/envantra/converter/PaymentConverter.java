package com.project.envantra.converter;

import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.dto.PaymentDto;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentCreateRequest;
import com.project.envantra.model.request.PaymentUpdateRequest;
import com.project.envantra.model.response.PaymentResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.project.envantra.util.LoginContext.getUser;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentConverter {

    public static PaymentDto toDto(PaymentCreateRequest request, BrokerDto broker) {
        return PaymentDto.builder()
                .originalPaymentId(null)
                .user(getUser())
                .broker(broker)
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .status(PaymentStatus.ACTIVE)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public static PaymentDto toDto(PaymentUpdateRequest request, BrokerDto broker, Long originalPaymentId) {
        return PaymentDto.builder()
                .originalPaymentId(originalPaymentId)
                .user(getUser())
                .broker(broker)
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .status(PaymentStatus.ACTIVE)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }

    public static PaymentDto toDto(PaymentEntity paymentEntity, BrokerDto broker) {
        return PaymentDto.builder()
                .originalPaymentId(paymentEntity.getOriginalPaymentId())
                .user(getUser())
                .broker(broker)
                .price(paymentEntity.getPrice())
                .type(paymentEntity.getType())
                .status(paymentEntity.getStatus())
                .cancelReason(paymentEntity.getCancelReason())
                .createdDate(paymentEntity.getCreatedDate())
                .lastModifiedDate(paymentEntity.getLastModifiedDate())
                .build();
    }

    public static PaymentEntity toEntity(PaymentDto paymentDto) {
        return PaymentEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(paymentDto.getBroker().getBrokerId())
                .originalPaymentId(paymentDto.getOriginalPaymentId())
                .price(paymentDto.getPrice())
                .type(paymentDto.getType())
                .status(paymentDto.getStatus())
                .cancelReason(paymentDto.getCancelReason())
                .build();
    }

    public static PaymentResponse toResponse(PaymentEntity paymentEntity, BrokerDto broker, String downloadUrl) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(paymentEntity.getPrice())
                .paymentType(paymentEntity.getType())
                .status(paymentEntity.getStatus())
                .downloadUrl(downloadUrl)
                .build();
    }
}
