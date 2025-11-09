package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.DOCUMENT_NOT_FOUND;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentNotFoundException extends EnvantraRuntimeException {

    public DocumentNotFoundException() {
        super(DOCUMENT_NOT_FOUND, NOT_FOUND, "Document not found!");
    }
}
