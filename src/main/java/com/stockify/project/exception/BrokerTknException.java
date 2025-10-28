package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_TKN_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerTknException extends StockifyRuntimeException {

    public BrokerTknException() {
        super(BROKER_TKN_REQUIRED, NOT_FOUND, "Broker Tkn Required!");
    }
}
