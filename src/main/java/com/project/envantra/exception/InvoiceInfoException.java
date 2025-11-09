package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVOICE_INFO_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvoiceInfoException extends EnvantraRuntimeException {

    public InvoiceInfoException() {
        super(INVOICE_INFO_REQUIRED, NOT_FOUND, "Company Invoice Information Required For Invoice PDF!");
    }
}
