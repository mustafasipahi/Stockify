package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.HAS_AVAILABLE_PRODUCT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class HasAvailableProductException extends EnvantraRuntimeException {

    public HasAvailableProductException(Long categoryId) {
        super(HAS_AVAILABLE_PRODUCT, NOT_FOUND, "Has available products in category with id: " + categoryId);
    }
}
