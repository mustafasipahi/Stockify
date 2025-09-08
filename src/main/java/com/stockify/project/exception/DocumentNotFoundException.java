package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.DOCUMENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentNotFoundException extends StockifyRuntimeException {

    public DocumentNotFoundException() {
        super(DOCUMENT_NOT_FOUND, NOT_FOUND, "Document not found!");
    }
}
