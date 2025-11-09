package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVALID_IMAGE_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidImageTypeException extends StockifyRuntimeException {

    public InvalidImageTypeException() {
        super(INVALID_IMAGE_TYPE, NOT_FOUND, "Invalid image type!");
    }
}
