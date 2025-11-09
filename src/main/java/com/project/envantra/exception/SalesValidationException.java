package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.MULTIPLE_PRODUCT_ID;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class SalesValidationException extends EnvantraRuntimeException {

    public SalesValidationException(Long productId) {
        super(MULTIPLE_PRODUCT_ID, NOT_FOUND, "Multiple productId: " + productId + " found!");
    }
}
