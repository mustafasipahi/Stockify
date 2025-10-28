package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.HAS_AVAILABLE_INVENTORY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class HasAvailableInventoryException extends StockifyRuntimeException {

    public HasAvailableInventoryException(Long productId) {
        super(HAS_AVAILABLE_INVENTORY, NOT_FOUND, "Has available inventory for product id: " + productId);
    }
}
