package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.PDF_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class PdfException extends EnvantraRuntimeException {

    public PdfException() {
        super(PDF_ERROR, NOT_FOUND, "Pdf Error!");
    }
}
