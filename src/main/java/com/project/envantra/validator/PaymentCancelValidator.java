package com.project.envantra.validator;

import com.project.envantra.enums.PaymentStatus;
import com.project.envantra.exception.EnvantraRuntimeException;
import com.project.envantra.model.entity.PaymentEntity;
import com.project.envantra.model.request.PaymentCancelRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentCancelValidator {

    public static void validate(PaymentCancelRequest request, PaymentEntity existingPayment) {
        validateRequest(request);
        validatePaymentStatus(existingPayment);
    }

    private static void validateRequest(PaymentCancelRequest request) {
        if (request == null) {
            throw new EnvantraRuntimeException("Payment cancel request cannot be null");
        }
        if (request.getPaymentId() == null) {
            throw new EnvantraRuntimeException("Payment ID cannot be null");
        }
        if (StringUtils.isBlank(request.getCancelReason())) {
            throw new EnvantraRuntimeException("Cancel reason cannot be empty");
        }
    }

    private static void validatePaymentStatus(PaymentEntity existingPayment) {
        if (existingPayment.getStatus() == PaymentStatus.CANCELLED) {
            throw new EnvantraRuntimeException("Payment is already cancelled");
        }
        if (existingPayment.getStatus() == PaymentStatus.UPDATED) {
            throw new EnvantraRuntimeException("Cannot cancel an updated payment. Please cancel the latest version.");
        }
    }
}