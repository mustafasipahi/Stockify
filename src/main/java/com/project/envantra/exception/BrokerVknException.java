package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_VKN_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerVknException extends EnvantraRuntimeException {

    public BrokerVknException() {
        super(BROKER_VKN_REQUIRED, NOT_FOUND, "Broker Vkn Required!");
    }
}
