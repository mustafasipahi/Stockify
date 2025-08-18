package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PRODUCT_PRICE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductPriceException extends StockifyRuntimeException {

    public ProductPriceException() {
        super(PRODUCT_PRICE_REQUIRED, NOT_FOUND, "Product Price Required!");
    }
}
