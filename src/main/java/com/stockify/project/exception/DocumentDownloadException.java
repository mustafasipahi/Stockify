package com.stockify.project.exception;

import static com.stockify.project.constant.ErrorCodes.DOCUMENT_DOWNLOAD_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

public class DocumentDownloadException extends StockifyRuntimeException {

    public DocumentDownloadException() {
        super(DOCUMENT_DOWNLOAD_ERROR, NOT_FOUND, "Document Download Error!");
    }
}
