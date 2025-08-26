package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.CATEGORY_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryIdException extends StockifyRuntimeException {

    public CategoryIdException() {
        super(CATEGORY_ID_REQUIRED, NOT_FOUND, "Category Id Required!");
    }
}
