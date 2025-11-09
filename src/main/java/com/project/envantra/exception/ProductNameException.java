package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PRODUCT_NAME_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNameException extends EnvantraRuntimeException {

    public ProductNameException() {
        super(PRODUCT_NAME_REQUIRED, NOT_FOUND, "Product Name Required!");
    }
}
