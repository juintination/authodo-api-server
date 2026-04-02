package com.example.authodo.adapter.in.web.security.handler;

import com.example.authodo.adapter.in.web.security.exception.JwtAuthenticationException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.common.error.ErrorResponse;
import com.example.authodo.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void commence(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException ex
    ) throws IOException {
        ErrorCode code = ErrorCode.UNAUTHORIZED;

        if (ex instanceof JwtAuthenticationException jwtEx) {
            code = jwtEx.getErrorCode();
        }

        String message = messageSource.getMessage(
            code.getMessageKey(),
            null,
            Locale.getDefault()
        );

        ErrorResponse error = ErrorResponse.of(
            request.getRequestURI(),
            request.getMethod(),
            code.name()
        );

        ApiResponse<?> body = ApiResponse.error(error, message);

        response.setStatus(code.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");

        objectMapper.writeValue(response.getWriter(), body);
    }
}
