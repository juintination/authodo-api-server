package com.example.authodo.adapter.in.web.auth;

import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.LoginRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.SignupRequest;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.TokenResponse;
import com.example.authodo.adapter.in.web.auth.dto.AuthDtos.UserInfoResponse;
import com.example.authodo.adapter.in.web.common.response.ApiResponse;
import com.example.authodo.adapter.in.web.security.jwt.JwtTokenProvider;
import com.example.authodo.adapter.in.web.security.util.SecurityUtil;
import com.example.authodo.domain.auth.port.in.AuthUseCasePort;
import com.example.authodo.domain.user.User;
import jakarta.validation.Valid;
import java.util.List;
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
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtil securityUtil;

    @PostMapping("/signup")
    public ApiResponse<TokenResponse> signup(
        @Valid @RequestBody SignupRequest request
    ) {
        User user = authUseCasePort.signup(
            request.email(),
            request.password(),
            request.nickname()
        );

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), List.of());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return ApiResponse.success(TokenResponse.of(accessToken, refreshToken));
    }

    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(
        @Valid @RequestBody LoginRequest request
    ) {
        User user = authUseCasePort.login(
            request.email(),
            request.password()
        );

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), List.of());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return ApiResponse.success(TokenResponse.of(accessToken, refreshToken));
    }

    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getMyInfo() {
        Long userId = securityUtil.getCurrentUserId();
        User user = authUseCasePort.getById(userId);
        return ApiResponse.success(UserInfoResponse.from(user));
    }
}
