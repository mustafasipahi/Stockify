package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.CATEGORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNotFoundException extends EnvantraRuntimeException {

    public CategoryNotFoundException(Long categoryId) {
        super(CATEGORY_NOT_FOUND, NOT_FOUND, "Category " + categoryId + " not found!");
    }
}
