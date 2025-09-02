package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.TAX_RATE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TaxRateException extends StockifyRuntimeException {

    public TaxRateException() {
        super(TAX_RATE_REQUIRED, NOT_FOUND, "Tax Rate Required!");
    }
}
