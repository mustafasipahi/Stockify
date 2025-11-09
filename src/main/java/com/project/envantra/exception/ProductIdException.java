package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PRODUCT_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductIdException extends EnvantraRuntimeException {

    public ProductIdException() {
        super(PRODUCT_ID_REQUIRED, NOT_FOUND, "Product Id Required!");
    }
}
