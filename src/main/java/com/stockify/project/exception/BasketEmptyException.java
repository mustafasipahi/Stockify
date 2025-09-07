package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.BASKET_EMPTY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BasketEmptyException extends StockifyRuntimeException {

    public BasketEmptyException() {
        super(BASKET_EMPTY, NOT_FOUND, "Basket empty!");
    }
}
