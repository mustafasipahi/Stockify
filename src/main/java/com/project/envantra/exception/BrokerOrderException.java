package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVALID_ORDER_NO;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BrokerOrderException extends EnvantraRuntimeException {

    public BrokerOrderException(Long orderNo) {
        super(INVALID_ORDER_NO, NOT_FOUND, "Invalid Order No " + orderNo + " !");
    }
}
