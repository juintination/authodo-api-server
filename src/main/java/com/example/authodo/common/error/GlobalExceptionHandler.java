package com.example.authodo.common.error;

import com.example.authodo.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, Locale locale) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        String msg = messageSource.getMessage("error.common.invalid-argument", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.of(errors, msg));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
            ConstraintViolationException ex, Locale locale) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> errors.put(v.getPropertyPath().toString(), v.getMessage()));

        String msg = messageSource.getMessage("error.common.invalid-argument", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.of(errors, msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleNotReadable(
            HttpMessageNotReadableException ex, Locale locale) {

        String msg = messageSource.getMessage("error.common.invalid-argument", null, locale);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.of(Map.of(), msg));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusiness(BusinessException ex,
                                                                     HttpServletRequest req,
                                                                     Locale locale) {
        ErrorCode code = ex.getErrorCode();
        String message = messageSource.getMessage(code.getMessageKey(), ex.getMessageArgs(), locale);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .path(req.getRequestURI())
                .code(code.name())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(ApiResponse.of(error, message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEtc(Exception ex,
                                                                HttpServletRequest req,
                                                                Locale locale) {
        ErrorCode code = ErrorCode.INTERNAL_ERROR;
        String message = messageSource.getMessage(code.getMessageKey(), null, locale);

        ErrorResponse error = ErrorResponse.builder()
                .timestamp(Instant.now().toString())
                .path(req.getRequestURI())
                .code(code.name())
                .build();

        return ResponseEntity.status(code.getHttpStatus()).body(ApiResponse.of(error, message));
    }

}
