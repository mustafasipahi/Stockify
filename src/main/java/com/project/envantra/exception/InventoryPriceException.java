package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVENTORY_PRICE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryPriceException extends EnvantraRuntimeException {

    public InventoryPriceException() {
        super(INVENTORY_PRICE_REQUIRED, NOT_FOUND, "Inventory Price Required!");
    }
}
