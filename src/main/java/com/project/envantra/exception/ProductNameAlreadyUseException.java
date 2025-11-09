package com.project.envantra.exception;

import java.util.Map;

import static com.project.envantra.constant.ErrorCodes.PRODUCT_NAME_ALREADY_USED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class ProductNameAlreadyUseException extends EnvantraRuntimeException {

    public ProductNameAlreadyUseException() {
        super(PRODUCT_NAME_ALREADY_USED, NOT_FOUND, "Product Name Already Used!");
    }

    public ProductNameAlreadyUseException(Long id) {
        super(PRODUCT_NAME_ALREADY_USED, NOT_FOUND, "Product Name Already Used!", Map.of("productId", id));
    }
}
