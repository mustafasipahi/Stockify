package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.INVALID_DOCUMENT_SIZE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidDocumentSizeException extends StockifyRuntimeException {

    public InvalidDocumentSizeException(String maxFileSize) {
        super(INVALID_DOCUMENT_SIZE, NOT_FOUND, "Invalid document size Max: " + maxFileSize);
    }
}
