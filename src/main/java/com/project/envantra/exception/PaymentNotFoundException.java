package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PAYMENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentNotFoundException extends EnvantraRuntimeException {

    public PaymentNotFoundException() {
        super(PAYMENT_NOT_FOUND, NOT_FOUND, "Payment not found!");
    }

    public PaymentNotFoundException(Long paymentId) {
        super(PAYMENT_NOT_FOUND, NOT_FOUND, "Payment " + paymentId + " not found!");
    }
}
