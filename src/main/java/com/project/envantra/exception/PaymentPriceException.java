package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PAYMENT_PRICE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentPriceException extends EnvantraRuntimeException {

    public PaymentPriceException() {
        super(PAYMENT_PRICE_REQUIRED, NOT_FOUND, "Payment Price Required!");
    }
}
