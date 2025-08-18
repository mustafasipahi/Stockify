package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNameException extends StockifyRuntimeException {

    public ProductNameException() {
        super(PRODUCT_NAME_REQUIRED, NOT_FOUND, "Product Name Required!");
    }
}
