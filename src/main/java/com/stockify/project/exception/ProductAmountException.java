package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_AMOUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductAmountException extends StockifyRuntimeException {

    public ProductAmountException() {
        super(PRODUCT_AMOUNT_REQUIRED, NOT_FOUND, "Product Amount Required!");
    }
}
