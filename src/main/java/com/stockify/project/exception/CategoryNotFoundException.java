package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.CATEGORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNotFoundException extends StockifyRuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super(CATEGORY_NOT_FOUND, NOT_FOUND, "Category " + categoryId + " not found!");
    }
}
