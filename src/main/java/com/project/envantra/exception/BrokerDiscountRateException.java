package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_DISCOUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerDiscountRateException extends EnvantraRuntimeException {

    public BrokerDiscountRateException() {
        super(BROKER_DISCOUNT_REQUIRED, NOT_FOUND, "Broker Discount Rate Required!");
    }
}
