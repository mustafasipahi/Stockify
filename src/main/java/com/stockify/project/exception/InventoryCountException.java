package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVENTORY_COUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryCountException extends StockifyRuntimeException {

    public InventoryCountException() {
        super(INVENTORY_COUNT_REQUIRED, NOT_FOUND, "Inventory Count Required!");
    }
}
