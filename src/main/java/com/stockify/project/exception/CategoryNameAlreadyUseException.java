package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.CATEGORY_NAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNameAlreadyUseException extends StockifyRuntimeException {

    public CategoryNameAlreadyUseException() {
        super(CATEGORY_NAME_ALREADY_USED, NOT_FOUND, "Category Name Already Used!");
    }
}
