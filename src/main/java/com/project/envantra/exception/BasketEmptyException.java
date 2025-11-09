package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.BASKET_EMPTY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class BasketEmptyException extends EnvantraRuntimeException {

    public BasketEmptyException() {
        super(BASKET_EMPTY, NOT_FOUND, "Basket empty!");
    }
}
