package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PAYMENT_PRICE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PaymentPriceException extends StockifyRuntimeException {

    public PaymentPriceException() {
        super(PAYMENT_PRICE_REQUIRED, NOT_FOUND, "Payment Price Required!");
    }
}
