package com.stockify.project.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.stockify.project.exception.StockifyRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm:ss")
    private Date date;
    private String message;
    private int code;

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(new Date(), message, code);
    }

    public static ErrorResponse of(StockifyRuntimeException exception) {
        return new ErrorResponse(new Date(), exception.getMessage(), exception.getCode());
    }
}
