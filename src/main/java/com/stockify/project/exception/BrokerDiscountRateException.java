package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_DISCOUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerDiscountRateException extends StockifyRuntimeException {

    public BrokerDiscountRateException() {
        super(BROKER_DISCOUNT_REQUIRED, NOT_FOUND, "Broker Discount Rate Required!");
    }
}
