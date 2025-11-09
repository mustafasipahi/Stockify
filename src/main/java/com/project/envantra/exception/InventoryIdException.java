package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVENTORY_ID_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InventoryIdException extends EnvantraRuntimeException {

    public InventoryIdException() {
        super(INVENTORY_ID_REQUIRED, NOT_FOUND, "Inventory Id Required!");
    }
}
