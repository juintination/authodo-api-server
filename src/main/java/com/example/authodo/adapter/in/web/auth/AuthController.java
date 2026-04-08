package com.example.authodo.adapter.in.web.auth;

import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.LoginRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.RefreshTokenRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.SignupRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.TokenResponse;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.UserInfoResponse;
import com.example.authodo.adapter.in.web.common.response.ApiResponse;
import com.example.authodo.adapter.in.web.security.util.SecurityUtil;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.auth.port.in.AuthUseCasePort;
import com.example.authodo.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthUseCasePort authUseCasePort;
    private final SecurityUtil securityUtil;

    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        TokenResult result = authUseCasePort.signup(request.toCommand());

        return ApiResponse.success(TokenResponse.from(result));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        TokenResult result = authUseCasePort.login(request.toCommand());

        return ApiResponse.success(TokenResponse.from(result));
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getMyInfo() {
        User user = authUseCasePort.getById(securityUtil.getCurrentUserId());

        return ApiResponse.success(UserInfoResponse.from(user));
    }

    @PostMapping("/refresh")
    public ApiResponse<TokenResponse> refresh(
        @RequestBody RefreshTokenRequest request
    ) {
        TokenResult result = authUseCasePort.refresh(request.toCommand());

        return ApiResponse.success(TokenResponse.from(result));
    }
}
