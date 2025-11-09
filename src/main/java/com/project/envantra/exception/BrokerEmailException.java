package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_EMAIL_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerEmailException extends EnvantraRuntimeException {

    public BrokerEmailException() {
        super(BROKER_EMAIL_REQUIRED, NOT_FOUND, "Broker Email Required!");
    }
}
