package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVENTORY_CRITICAL_COUNT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryCriticalCountException extends EnvantraRuntimeException {

    public InventoryCriticalCountException() {
        super(INVENTORY_CRITICAL_COUNT_REQUIRED, NOT_FOUND, "Inventory Critical Count Required!");
    }
}
