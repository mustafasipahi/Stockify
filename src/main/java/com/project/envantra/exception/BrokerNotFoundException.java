package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BROKER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerNotFoundException extends EnvantraRuntimeException {

    public BrokerNotFoundException(Long brokerId) {
        super(BROKER_NOT_FOUND, NOT_FOUND, "Broker " + brokerId + " not found!");
    }
}
