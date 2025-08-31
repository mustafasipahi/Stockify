package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.exception.StockifyRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime date;
    private String message;
    private int code;

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(LocalDateTime.now(), message, code);
    }

    public static ErrorResponse of(StockifyRuntimeException exception) {
        return new ErrorResponse(LocalDateTime.now(), exception.getMessage(), exception.getCode());
    }
}
