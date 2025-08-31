package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.DOCUMENT_TYPE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentTypeRequiredException extends StockifyRuntimeException {

    public DocumentTypeRequiredException() {
        super(DOCUMENT_TYPE_REQUIRED, NOT_FOUND, "Document Type Required!");
    }
}
