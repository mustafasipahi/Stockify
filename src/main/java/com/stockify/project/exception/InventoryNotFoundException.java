package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVENTORY_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryNotFoundException extends StockifyRuntimeException {

    public InventoryNotFoundException(Long inventoryId) {
        super(INVENTORY_NOT_FOUND, NOT_FOUND, "Inventory " + inventoryId + " not found!");
    }
}
