package com.stockify.project.converter;

import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.dto.PaymentDto;
import com.stockify.project.model.entity.PaymentEntity;
import com.stockify.project.model.request.PaymentCreateRequest;
import com.stockify.project.model.response.PaymentResponse;
import com.stockify.project.repository.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.stockify.project.constant.DocumentNumberConstants.PAYMENT_DEFAULT;
import static com.stockify.project.constant.DocumentNumberConstants.PAYMENT_PREFIX;
import static com.stockify.project.util.DocumentUtil.getDownloadUrl;
import static com.stockify.project.util.TenantContext.getTenantId;

@Component
@AllArgsConstructor
public class PaymentConverter {

    private final PaymentRepository paymentRepository;

    public PaymentDto toDto(PaymentCreateRequest request) {
        return PaymentDto.builder()
                .brokerId(request.getBrokerId())
                .price(request.getPaymentPrice())
                .type(request.getPaymentType())
                .build();
    }

    public PaymentEntity toEntity(PaymentDto paymentDto) {
        String documentNumber = getDocumentNumber();
        paymentDto.setDocumentNumber(documentNumber);
        return PaymentEntity.builder()
                .documentNumber(documentNumber)
                .brokerId(paymentDto.getBrokerId())
                .price(paymentDto.getPrice())
                .type(paymentDto.getType())
                .tenantId(getTenantId())
                .build();
    }

    public PaymentResponse toResponse(PaymentEntity paymentEntity, BrokerDto broker, String documentId) {
        return PaymentResponse.builder()
                .firstName(broker.getFirstName())
                .lastName(broker.getLastName())
                .paymentPrice(paymentEntity.getPrice())
                .downloadUrl(getDownloadUrl(documentId))
                .build();
    }

    private String getDocumentNumber() {
        return Optional.ofNullable(paymentRepository.findMaxDocumentNumberNumeric())
                .map(lastDocumentNumber -> PAYMENT_PREFIX + (lastDocumentNumber + 1))
                .orElse(PAYMENT_PREFIX + PAYMENT_DEFAULT);
    }
}
