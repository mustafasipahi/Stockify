package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.UNAUTHENTICATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class UnauthenticatedException extends StockifyRuntimeException {

    public UnauthenticatedException() {
        super(UNAUTHENTICATED, UNAUTHORIZED, "Authentication error!");
    }
}
