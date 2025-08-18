package com.stockify.project.security.entrypoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockify.project.model.response.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;

import static com.stockify.project.constant.ErrorCodes.UNAUTHENTICATED;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        OutputStream out = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        mapper.writeValue(out, getError());
        out.flush();
    }

    private ErrorResponse getError() {
        return ErrorResponse.of(UNAUTHENTICATED, "Authentication Error!");
    }
}
