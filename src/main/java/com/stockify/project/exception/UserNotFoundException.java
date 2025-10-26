package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UserNotFoundException extends StockifyRuntimeException {

    public UserNotFoundException(Long userId) {
        super(USER_NOT_FOUND, NOT_FOUND, "User " + userId + " not found!");
    }
}
