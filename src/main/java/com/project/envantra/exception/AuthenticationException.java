package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.UNAUTHENTICATED;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public class AuthenticationException extends EnvantraRuntimeException {

    public AuthenticationException() {
        super(UNAUTHENTICATED, UNAUTHORIZED, "Authentication error!");
    }
}
