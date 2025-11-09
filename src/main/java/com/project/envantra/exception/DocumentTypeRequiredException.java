package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.DOCUMENT_TYPE_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentTypeRequiredException extends EnvantraRuntimeException {

    public DocumentTypeRequiredException() {
        super(DOCUMENT_TYPE_REQUIRED, NOT_FOUND, "Document Type Required!");
    }
}
