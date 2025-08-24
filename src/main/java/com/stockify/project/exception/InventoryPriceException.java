package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVENTORY_PRICE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryPriceException extends StockifyRuntimeException {

    public InventoryPriceException() {
        super(INVENTORY_PRICE_REQUIRED, NOT_FOUND, "Inventory Price Required!");
    }
}
