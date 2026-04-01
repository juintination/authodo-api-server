package com.example.authodo.common.error;

import java.time.Instant;
import java.util.List;

public record ErrorResponse(
    String timestamp,
    String path,
    String method,
    String code,
    List<FieldErrorDetail> errors
) {

    public static ErrorResponse of(
        String path,
        String method,
        String code
    ) {
        return new ErrorResponse(
            Instant.now().toString(),
            path,
            method,
            code,
            null
        );
    }

    public static ErrorResponse of(
        String path,
        String method,
        String code,
        List<FieldErrorDetail> errors
    ) {
        return new ErrorResponse(
            Instant.now().toString(),
            path,
            method,
            code,
            errors
        );
    }

    public record FieldErrorDetail(
        String field,
        String code,
        String message
    ) {

    }
}
