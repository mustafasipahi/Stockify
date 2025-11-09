package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVALID_CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidContentTypeException extends EnvantraRuntimeException {

    public InvalidContentTypeException() {
        super(INVALID_CONTENT_TYPE, NOT_FOUND, "Invalid Content Type!");
    }
}
