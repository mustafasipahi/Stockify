package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PRODUCT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNotFoundException extends EnvantraRuntimeException {

    public ProductNotFoundException() {
        super(PRODUCT_NOT_FOUND, NOT_FOUND, "Product not found!");
    }

    public ProductNotFoundException(Long productId) {
        super(PRODUCT_NOT_FOUND, NOT_FOUND, "Product " + productId + " not found!");
    }
}
