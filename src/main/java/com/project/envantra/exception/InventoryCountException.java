package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVENTORY_COUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryCountException extends EnvantraRuntimeException {

    public InventoryCountException() {
        super(INVENTORY_COUNT_REQUIRED, NOT_FOUND, "Inventory Count Required!");
    }
}
