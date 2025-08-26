package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.CATEGORY_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNameException extends StockifyRuntimeException {

    public CategoryNameException() {
        super(CATEGORY_NAME_REQUIRED, NOT_FOUND, "Category Name Required!");
    }
}
