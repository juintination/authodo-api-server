package com.example.authodo.common.response;

import lombok.Builder;

@Builder
public record ApiResponse<T>(
        T data,
        String message
) {
    public static <T> ApiResponse<T> of(T data, String message) {
        return ApiResponse.<T>builder()
                .data(data)
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return of(data, "success");
    }

    public static ApiResponse<Void> message(String message) {
        return of(null, message);
    }

}
