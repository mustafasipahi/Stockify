package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.KDV_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class KdvException extends StockifyRuntimeException {

    public KdvException() {
        super(KDV_REQUIRED, NOT_FOUND, "Kdv Required!");
    }
}
