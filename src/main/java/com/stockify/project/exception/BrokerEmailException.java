package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_EMAIL_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerEmailException extends StockifyRuntimeException {

    public BrokerEmailException() {
        super(BROKER_EMAIL_REQUIRED, NOT_FOUND, "Broker Email Required!");
    }
}
