package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVENTORY_CRITICAL_COUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryCriticalCountException extends StockifyRuntimeException {

    public InventoryCriticalCountException() {
        super(INVENTORY_CRITICAL_COUNT_REQUIRED, NOT_FOUND, "Inventory Critical Count Required!");
    }
}
