package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerIdException extends EnvantraRuntimeException {

    public BrokerIdException() {
        super(BROKER_ID_REQUIRED, NOT_FOUND, "Broker Id Required!");
    }
}
