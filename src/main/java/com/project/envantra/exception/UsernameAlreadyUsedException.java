package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.USERNAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.CONFLICT;

public class UsernameAlreadyUsedException extends EnvantraRuntimeException {

    public UsernameAlreadyUsedException() {
        super(USERNAME_ALREADY_USED, CONFLICT, "Username Already Used!");
    }
}
