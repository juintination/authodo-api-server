package com.example.authodo.adapter.in.web.auth.dto;

import com.example.authodo.domain.user.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

public class AuthDtos {

    public record SignupRequest(
        @Email(message = "{error.common.invalid-argument}")
        @NotBlank(message = "{error.common.invalid-argument}")
        String email,

        @NotBlank(message = "{error.common.invalid-argument}")
        @Size(min = 8, message = "{error.common.invalid-argument}")
        String password,

        @NotBlank(message = "{error.common.invalid-argument}")
        String nickname
    ) {

    }

    public record LoginRequest(
        @Email(message = "{error.common.invalid-argument}")
        @NotBlank(message = "{error.common.invalid-argument}")
        String email,

        @NotBlank(message = "{error.common.invalid-argument}")
        String password
    ) {

    }

    @Builder
    public record TokenResponse(
        String accessToken,
        String refreshToken
    ) {

        public static TokenResponse of(String accessToken, String refreshToken) {
            return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        }
    }

    @Builder
    public record UserInfoResponse(
        Long id,
        String email,
        String nickname
    ) {

        public static UserInfoResponse from(User user) {
            return UserInfoResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
        }
    }
}
