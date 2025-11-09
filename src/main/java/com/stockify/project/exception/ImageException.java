package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.IMAGE_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ImageException extends StockifyRuntimeException {

    public ImageException() {
        super(IMAGE_ERROR, NOT_FOUND, "Image Error!");
    }
}
