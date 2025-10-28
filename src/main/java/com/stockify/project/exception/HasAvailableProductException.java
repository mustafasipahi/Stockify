package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.HAS_AVAILABLE_PRODUCT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class HasAvailableProductException extends StockifyRuntimeException {

    public HasAvailableProductException(Long categoryId) {
        super(HAS_AVAILABLE_PRODUCT, NOT_FOUND, "Has available products in category with id: " + categoryId);
    }
}
