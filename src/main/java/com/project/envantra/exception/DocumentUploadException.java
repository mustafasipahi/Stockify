package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.DOCUMENT_UPLOAD_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentUploadException extends EnvantraRuntimeException {

    public DocumentUploadException() {
        super(DOCUMENT_UPLOAD_ERROR, NOT_FOUND, "Document Upload Error!");
    }
}
