package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.CATEGORY_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class CategoryNameException extends EnvantraRuntimeException {

    public CategoryNameException() {
        super(CATEGORY_NAME_REQUIRED, NOT_FOUND, "Category Name Required!");
    }
}
