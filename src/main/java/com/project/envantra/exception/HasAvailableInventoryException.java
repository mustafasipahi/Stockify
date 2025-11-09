package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.HAS_AVAILABLE_INVENTORY;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class HasAvailableInventoryException extends EnvantraRuntimeException {

    public HasAvailableInventoryException(Long productId) {
        super(HAS_AVAILABLE_INVENTORY, NOT_FOUND, "Has available inventory for product id: " + productId);
    }
}
