package com.example.authodo.adapter.in.web.auth.dto;

import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.command.SignupCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
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

        public SignupCommand toCommand() {
            return new SignupCommand(this.email, this.password, this.nickname);
        }
    }

    public record LoginRequest(
        @Email(message = "{error.common.invalid-argument}")
        @NotBlank(message = "{error.common.invalid-argument}")
        String email,

        @NotBlank(message = "{error.common.invalid-argument}")
        String password
    ) {

        public LoginCommand toCommand() {
            return new LoginCommand(this.email, this.password);
        }
    }

    @Builder
    public record TokenResponse(
        String accessToken,
        String refreshToken
    ) {

        public static TokenResponse from(TokenResult result) {
            return TokenResponse.builder()
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
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
