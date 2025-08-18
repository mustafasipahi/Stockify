package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.SEARCH_TEXT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductSearchException extends StockifyRuntimeException {

    public ProductSearchException() {
        super(SEARCH_TEXT_REQUIRED, NOT_FOUND, "Search Text Required!");
    }
}
