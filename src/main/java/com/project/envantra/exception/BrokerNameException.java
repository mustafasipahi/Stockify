package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerNameException extends EnvantraRuntimeException {

    public BrokerNameException(String message) {
        super(BROKER_NAME_REQUIRED, NOT_FOUND, message);
    }
}
