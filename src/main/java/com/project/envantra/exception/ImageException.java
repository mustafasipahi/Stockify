package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.IMAGE_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ImageException extends EnvantraRuntimeException {

    public ImageException() {
        super(IMAGE_ERROR, NOT_FOUND, "Image Error!");
    }
}
