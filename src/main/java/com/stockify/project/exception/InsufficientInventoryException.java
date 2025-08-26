package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.CATEGORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InsufficientInventoryException extends StockifyRuntimeException {

    public InsufficientInventoryException(String productName, Integer availableProductCount, Integer requestedProductCount) {
        super(CATEGORY_NOT_FOUND, NOT_FOUND, "Insufficient inventory for product: " + productName + ". Available: " + availableProductCount + ", Requested: " + requestedProductCount);
    }
}
