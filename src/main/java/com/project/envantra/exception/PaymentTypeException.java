package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PAYMENT_TYPE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentTypeException extends EnvantraRuntimeException {

    public PaymentTypeException() {
        super(PAYMENT_TYPE_REQUIRED, NOT_FOUND, "Payment Type Required!");
    }
}
