package com.stockify.project.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class StockifyRuntimeException extends RuntimeException {

    private final int code;
    private final HttpStatus status;

    public StockifyRuntimeException(int code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }
}
