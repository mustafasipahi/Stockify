package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVALID_CONTENT_TYPE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidContentTypeException extends StockifyRuntimeException {

    public InvalidContentTypeException() {
        super(INVALID_CONTENT_TYPE, NOT_FOUND, "Invalid Content Type!");
    }
}
