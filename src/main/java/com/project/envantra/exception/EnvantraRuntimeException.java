package com.project.envantra.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.io.Serial;
import java.util.Map;

import static com.project.envantra.constant.ErrorCodes.UNKNOWN_ERROR;

@Getter
public class EnvantraRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private final int code;
    private final HttpStatus status;
    private final transient Map<String, Object> properties;

    public EnvantraRuntimeException(String message) {
        this(UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR, message);
    }

    public EnvantraRuntimeException(int code, HttpStatus status, String message) {
        super(message);
        this.code = code;
        this.status = status;
        this.properties = null;
    }

    public EnvantraRuntimeException(int code, HttpStatus status, String message, Map<String, Object> properties) {
        super(message);
        this.code = code;
        this.status = status;
        this.properties = properties;
    }
}
