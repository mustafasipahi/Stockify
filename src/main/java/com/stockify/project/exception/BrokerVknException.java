package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BROKER_VKN_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerVknException extends StockifyRuntimeException {

    public BrokerVknException() {
        super(BROKER_VKN_REQUIRED, NOT_FOUND, "Broker Vkn Required!");
    }
}
