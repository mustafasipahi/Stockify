package com.project.envantra.exception;

import com.project.envantra.model.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.project.envantra.constant.ErrorCodes.UNKNOWN_ERROR;

@Slf4j
@ControllerAdvice
public class EnvantraExceptionAdvice {

    @ExceptionHandler(EnvantraRuntimeException.class)
    public ResponseEntity<ErrorResponse> handleException(EnvantraRuntimeException e) {
        log.error("Exception occurred!", e);
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(DocumentDownloadException.class)
    public ResponseEntity<ErrorResponse> handleDocumentDownloadException(DocumentDownloadException e) {
        log.error("Document exception occurred!", e);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(ErrorResponse.of(e));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("An unknown exception occurred!", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(UNKNOWN_ERROR, "An unknown exception occurred!"));
    }
}
