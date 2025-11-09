package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.CATEGORY_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryIdException extends EnvantraRuntimeException {

    public CategoryIdException() {
        super(CATEGORY_ID_REQUIRED, NOT_FOUND, "Category Id Required!");
    }
}
