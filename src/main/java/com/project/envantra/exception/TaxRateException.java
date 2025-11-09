package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.TAX_RATE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class TaxRateException extends EnvantraRuntimeException {

    public TaxRateException() {
        super(TAX_RATE_REQUIRED, NOT_FOUND, "Tax Rate Required!");
    }
}
