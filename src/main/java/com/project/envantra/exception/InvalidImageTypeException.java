package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVALID_IMAGE_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidImageTypeException extends EnvantraRuntimeException {

    public InvalidImageTypeException() {
        super(INVALID_IMAGE_TYPE, NOT_FOUND, "Invalid image type!");
    }
}
