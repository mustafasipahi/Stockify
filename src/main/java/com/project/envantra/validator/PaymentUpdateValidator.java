package com.project.envantra.validator;

import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.exception.EnvantraRuntimeException;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentUpdateRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentUpdateValidator {

    public static void validate(PaymentUpdateRequest request, PaymentEntity existingPayment) {
        validateRequest(request);
        validatePaymentStatus(existingPayment);
        validatePriceChange(request.getPaymentPrice());
    }

    private static void validateRequest(PaymentUpdateRequest request) {
        if (request == null) {
            throw new EnvantraRuntimeException("Payment update request cannot be null");
        }
        if (request.getPaymentId() == null) {
            throw new EnvantraRuntimeException("Payment ID cannot be null");
        }
        if (request.getPaymentPrice() == null) {
            throw new EnvantraRuntimeException("Price cannot be null");
        }
        if (request.getPaymentType() == null) {
            throw new EnvantraRuntimeException("Payment type cannot be null");
        }
    }

    private static void validatePaymentStatus(PaymentEntity existingPayment) {
        if (existingPayment.getStatus() != PaymentStatus.ACTIVE) {
            throw new EnvantraRuntimeException("Only active payments can be updated. Current status: " + existingPayment.getStatus());
        }
    }

    private static void validatePriceChange(BigDecimal newPrice) {
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new EnvantraRuntimeException("Price must be greater than zero");
        }
    }
}