package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.USERNAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.CONFLICT;

public class UsernameAlreadyUsedException extends StockifyRuntimeException {

    public UsernameAlreadyUsedException() {
        super(USERNAME_ALREADY_USED, CONFLICT, "Username Already Used!");
    }
}
