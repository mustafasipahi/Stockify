package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.DOCUMENT_UPLOAD_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentUploadException extends StockifyRuntimeException {

    public DocumentUploadException() {
        super(DOCUMENT_UPLOAD_ERROR, NOT_FOUND, "Document Upload Error!");
    }
}
