package com.example.authodo.adapter.in.web.security.handler;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.error.ErrorResponse;
import com.example.authodo.adapter.in.web.common.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException ex
    ) throws IOException {
        ErrorCode code = ErrorCode.FORBIDDEN;

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
