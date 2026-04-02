package com.example.authodo.adapter.in.web.common.handler;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.error.ErrorResponse;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.adapter.in.web.common.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * @Valid (RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpServletRequest req,
        Locale locale
    ) {

        log.warn("Validation failed: {}", ex.getMessage());

        List<ErrorResponse.FieldErrorDetail> errors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(err -> new ErrorResponse.FieldErrorDetail(
                err.getField(),
                err.getCode(),
                err.getDefaultMessage()
            ))
            .collect(Collectors.toList());

        String message = messageSource.getMessage(
            "error.common.invalid-argument", null, locale
        );

        ErrorResponse response = ErrorResponse.of(
            req.getRequestURI(),
            req.getMethod(),
            ErrorCode.INVALID_ARGUMENT.name(),
            errors
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(response, message));
    }

    /**
     * @Validated (Query, PathVariable)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleConstraintViolation(
        ConstraintViolationException ex,
        HttpServletRequest req,
        Locale locale
    ) {

        log.warn("Constraint violation: {}", ex.getMessage());

        List<ErrorResponse.FieldErrorDetail> errors = ex.getConstraintViolations()
            .stream()
            .map(v -> new ErrorResponse.FieldErrorDetail(
                extractField(v.getPropertyPath().toString()),
                v.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(),
                v.getMessage()
            ))
            .collect(Collectors.toList());

        String message = messageSource.getMessage(
            "error.common.invalid-argument", null, locale
        );

        ErrorResponse response = ErrorResponse.of(
            req.getRequestURI(),
            req.getMethod(),
            ErrorCode.INVALID_ARGUMENT.name(),
            errors
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(response, message));
    }

    /**
     * JSON 파싱 에러
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleNotReadable(
        HttpMessageNotReadableException ex,
        HttpServletRequest req,
        Locale locale
    ) {

        log.warn("Malformed request body", ex);

        String message = messageSource.getMessage(
            "error.common.invalid-request-body", null, locale
        );

        ErrorResponse response = ErrorResponse.of(
            req.getRequestURI(),
            req.getMethod(),
            ErrorCode.INVALID_ARGUMENT.name()
        );

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(response, message));
    }

    /**
     * 비즈니스 예외
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleBusiness(
        BusinessException ex,
        HttpServletRequest request,
        Locale locale
    ) {

        ErrorCode code = ex.getErrorCode();

        String message = messageSource.getMessage(
            code.getMessageKey(),
            ex.getMessageArgs(),
            locale
        );

        log.info("Business exception: code={}, message={}", code, message);

        ErrorResponse response = ErrorResponse.of(
            request.getRequestURI(),
            request.getMethod(),
            code.name()
        );

        return ResponseEntity
            .status(code.getHttpStatus())
            .body(ApiResponse.error(response, message));
    }

    /**
     * 예상 못한 예외
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<ErrorResponse>> handleEtc(
        Exception ex,
        HttpServletRequest request,
        Locale locale
    ) throws Exception {

        if (ex instanceof NoResourceFoundException || ex instanceof NoHandlerFoundException) {
            throw ex;
        }

        log.error("Unhandled exception", ex);

        ErrorCode code = ErrorCode.INTERNAL_ERROR;

        String message = messageSource.getMessage(
            code.getMessageKey(),
            null,
            locale
        );

        ErrorResponse response = ErrorResponse.of(
            request.getRequestURI(),
            request.getMethod(),
            code.name()
        );

        return ResponseEntity
            .status(code.getHttpStatus())
            .body(ApiResponse.error(response, message));
    }

    /**
     * propertyPath 정제
     */
    private String extractField(String path) {
        int lastDot = path.lastIndexOf('.');
        return (lastDot != -1) ? path.substring(lastDot + 1) : path;
    }
}
