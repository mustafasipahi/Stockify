package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductIdException extends StockifyRuntimeException {

    public ProductIdException() {
        super(PRODUCT_ID_REQUIRED, NOT_FOUND, "Product Id Required!");
    }
}
