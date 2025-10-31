package com.example.authodo.common.error;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ErrorResponse(
        String timestamp,
        String path,
        String code
) {}
