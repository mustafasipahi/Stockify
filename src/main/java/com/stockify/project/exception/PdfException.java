package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.PDF_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PdfException extends StockifyRuntimeException {

    public PdfException() {
        super(PDF_ERROR, NOT_FOUND, "Pdf Error!");
    }
}
