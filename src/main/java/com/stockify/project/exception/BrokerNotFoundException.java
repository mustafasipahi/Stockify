package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerNotFoundException extends StockifyRuntimeException {

    public BrokerNotFoundException(Long brokerId) {
        super(BROKER_NOT_FOUND, NOT_FOUND, "Broker " + brokerId + " not found!");
    }
}
