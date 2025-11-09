package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.CATEGORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InsufficientInventoryException extends EnvantraRuntimeException {

    public InsufficientInventoryException(String productName, Integer availableProductCount, Integer requestedProductCount) {
        super(CATEGORY_NOT_FOUND, NOT_FOUND, "Insufficient inventory for product: " + productName + ". Available: " + availableProductCount + ", Requested: " + requestedProductCount);
    }
}
