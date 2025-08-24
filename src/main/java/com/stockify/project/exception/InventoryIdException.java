package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVENTORY_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryIdException extends StockifyRuntimeException {

    public InventoryIdException() {
        super(INVENTORY_ID_REQUIRED, NOT_FOUND, "Inventory Id Required!");
    }
}
