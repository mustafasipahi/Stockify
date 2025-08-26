package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.MULTIPLE_PRODUCT_ID;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class SalesValidationException extends StockifyRuntimeException {

    public SalesValidationException(Long productId) {
        super(MULTIPLE_PRODUCT_ID, NOT_FOUND, "Multiple productId: " + productId + " found!");
    }
}
