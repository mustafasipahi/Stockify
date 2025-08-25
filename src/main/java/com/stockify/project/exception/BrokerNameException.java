package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerNameException extends StockifyRuntimeException {

    public BrokerNameException(String message) {
        super(BROKER_NAME_REQUIRED, NOT_FOUND, message);
    }
}
