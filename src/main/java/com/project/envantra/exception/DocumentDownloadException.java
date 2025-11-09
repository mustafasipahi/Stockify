package com.project.envantra.exception;

import static com.project.envantra.constant.ErrorCodes.DOCUMENT_DOWNLOAD_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentDownloadException extends EnvantraRuntimeException {

    public DocumentDownloadException() {
        super(DOCUMENT_DOWNLOAD_ERROR, NOT_FOUND, "Document Download Error!");
    }
}
