package com.example.authodo.application.auth;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.adapter.in.web.security.jwt.JwtTokenProvider;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.auth.port.out.RefreshTokenRepositoryPort;
import com.example.authodo.domain.user.enums.UserRole;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepositoryPort refreshTokenRepositoryPort;

    public TokenResult issue(Long userId, Set<UserRole> roles) {
        List<String> rolesStr = roles.stream().map(UserRole::name).toList();
        String accessToken = jwtTokenProvider.createAccessToken(userId, rolesStr);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        long ttl = jwtTokenProvider.getRefreshExpirationMs();

        refreshTokenRepositoryPort.save(userId, refreshToken, ttl);

        return new TokenResult(accessToken, refreshToken);
    }

    public Long extractUserIdFromRefreshToken(String refreshToken) {
        return jwtTokenProvider.getUserId(refreshToken);
    }

    public TokenResult refresh(Long userId, Set<UserRole> roles, String refreshToken) {
        String stored = refreshTokenRepositoryPort.findByUserId(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN));

        if (!stored.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_REFRESH_TOKEN);
        }

        refreshTokenRepositoryPort.delete(userId);

        return issue(userId, roles);
    }
}
