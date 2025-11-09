package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.CATEGORY_NAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNameAlreadyUseException extends EnvantraRuntimeException {

    public CategoryNameAlreadyUseException() {
        super(CATEGORY_NAME_ALREADY_USED, NOT_FOUND, "Category Name Already Used!");
    }
}
