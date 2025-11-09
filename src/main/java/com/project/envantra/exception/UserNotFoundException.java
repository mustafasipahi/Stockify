package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.USER_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class UserNotFoundException extends EnvantraRuntimeException {

    public UserNotFoundException(Long userId) {
        super(USER_NOT_FOUND, NOT_FOUND, "User " + userId + " not found!");
    }
}
