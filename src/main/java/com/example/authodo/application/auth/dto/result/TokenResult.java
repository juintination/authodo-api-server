package com.example.authodo.application.auth.dto.result;

public record TokenResult(
    String accessToken,
    String refreshToken
) {

}
