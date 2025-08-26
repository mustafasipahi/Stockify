package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerIdException extends StockifyRuntimeException {

    public BrokerIdException() {
        super(BROKER_ID_REQUIRED, NOT_FOUND, "Broker Id Required!");
    }
}
