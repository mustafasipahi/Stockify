package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNotFoundException extends StockifyRuntimeException {

    public ProductNotFoundException() {
        super(PRODUCT_NOT_FOUND, NOT_FOUND, "Product not found!");
    }

    public ProductNotFoundException(Long productId) {
        super(PRODUCT_NOT_FOUND, NOT_FOUND, "Product " + productId + " not found!");
    }
}
