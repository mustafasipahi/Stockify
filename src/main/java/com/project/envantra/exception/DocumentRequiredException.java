package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.DOCUMENT_REQUIRED;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentRequiredException extends EnvantraRuntimeException {

    public DocumentRequiredException() {
        super(DOCUMENT_REQUIRED, NOT_FOUND, "Document Required!");
    }
}
