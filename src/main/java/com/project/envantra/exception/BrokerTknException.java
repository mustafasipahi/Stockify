package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_TKN_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerTknException extends EnvantraRuntimeException {

    public BrokerTknException() {
        super(BROKER_TKN_REQUIRED, NOT_FOUND, "Broker Tkn Required!");
    }
}
