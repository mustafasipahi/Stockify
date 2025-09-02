package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PAYMENT_TYPE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentTypeException extends StockifyRuntimeException {

    public PaymentTypeException() {
        super(PAYMENT_TYPE_REQUIRED, NOT_FOUND, "Payment Type Required!");
    }
}
