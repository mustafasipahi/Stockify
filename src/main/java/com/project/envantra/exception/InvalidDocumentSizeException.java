package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.INVALID_DOCUMENT_SIZE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class InvalidDocumentSizeException extends EnvantraRuntimeException {

    public InvalidDocumentSizeException(String maxFileSize) {
        super(INVALID_DOCUMENT_SIZE, NOT_FOUND, "Invalid document size Max: " + maxFileSize);
    }
}
