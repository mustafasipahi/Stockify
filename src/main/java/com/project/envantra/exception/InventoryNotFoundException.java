package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVENTORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryNotFoundException extends EnvantraRuntimeException {

    public InventoryNotFoundException(Long inventoryId) {
        super(INVENTORY_NOT_FOUND, NOT_FOUND, "Inventory " + inventoryId + " not found!");
    }
}
