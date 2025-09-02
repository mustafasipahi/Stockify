package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.DOCUMENT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentRequiredException extends StockifyRuntimeException {

    public DocumentRequiredException() {
        super(DOCUMENT_REQUIRED, NOT_FOUND, "Document Required!");
    }
}
