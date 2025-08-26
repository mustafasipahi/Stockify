package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.UNAUTHENTICATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class AuthenticationException extends StockifyRuntimeException {

    public AuthenticationException() {
        super(UNAUTHENTICATED, UNAUTHORIZED, "Authentication error!");
    }
}
