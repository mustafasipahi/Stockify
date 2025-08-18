package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_NAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNameAlreadyUseException extends StockifyRuntimeException {

    public ProductNameAlreadyUseException() {
        super(PRODUCT_NAME_ALREADY_USED, NOT_FOUND, "Product Name Already Used!");
    }
}
