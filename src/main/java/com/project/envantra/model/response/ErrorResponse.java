package com.project.envantra.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.project.envantra.exception.EnvantraRuntimeException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static com.project.envantra.util.DateUtil.getTime;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Long date;
    private String message;
    private int code;

    public static ErrorResponse of(int code, String message) {
        return new ErrorResponse(getTime(LocalDateTime.now()), message, code);
    }

    public static ErrorResponse of(EnvantraRuntimeException exception) {
        return new ErrorResponse(getTime(LocalDateTime.now()), exception.getMessage(), exception.getCode());
    }
}
