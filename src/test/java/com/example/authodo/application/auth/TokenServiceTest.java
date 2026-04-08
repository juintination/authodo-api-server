package com.example.authodo.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.adapter.in.web.security.jwt.JwtTokenProvider;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.auth.port.out.RefreshTokenRepositoryPort;
import com.example.authodo.domain.user.enums.UserRole;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("TokenService 단위 테스트")
class TokenServiceTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private RefreshTokenRepositoryPort refreshTokenRepository;

    @InjectMocks
    private TokenService tokenService;

    @Test
    @DisplayName("issue() - 정상 발급 및 저장")
    void issue_success() {
        Long userId = 1L;
        Set<UserRole> roles = Set.of(UserRole.USER);

        given(jwtTokenProvider.createAccessToken(userId, List.of("USER"))).willReturn("access-token");
        given(jwtTokenProvider.createRefreshToken(userId)).willReturn("refresh-token");
        given(jwtTokenProvider.getRefreshExpirationMs()).willReturn(1000L);

        TokenResult result = tokenService.issue(userId, roles);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        verify(refreshTokenRepository).save(userId, "refresh-token", 1000L);
    }

    @Test
    @DisplayName("refresh() - 정상 재발급")
    void refresh_success() {
        Long userId = 1L;
        Set<UserRole> roles = Set.of(UserRole.USER);
        String oldToken = "refresh-token";
        String newAccessToken = "new-access-token";
        String newRefreshToken = "new-refresh-token";

        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.of(oldToken));
        given(jwtTokenProvider.createAccessToken(userId, List.of("USER"))).willReturn(newAccessToken);
        given(jwtTokenProvider.createRefreshToken(userId)).willReturn(newRefreshToken);
        given(jwtTokenProvider.getRefreshExpirationMs()).willReturn(2000L);

        TokenResult result = tokenService.refresh(userId, roles, oldToken);

        assertThat(result.accessToken()).isEqualTo(newAccessToken);
        assertThat(result.refreshToken()).isEqualTo(newRefreshToken);

        verify(refreshTokenRepository).delete(userId);
        verify(refreshTokenRepository).save(userId, newRefreshToken, 2000L);
    }

    @Test
    @DisplayName("refresh() - 저장된 토큰 없음")
    void refresh_notFound() {
        Long userId = 1L;
        Set<UserRole> roles = Set.of(UserRole.USER);
        String token = "refresh-token";

        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> tokenService.refresh(userId, roles, token))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_INVALID_REFRESH_TOKEN.name());
    }

    @Test
    @DisplayName("refresh() - 토큰 불일치")
    void refresh_mismatch() {
        Long userId = 1L;
        Set<UserRole> roles = Set.of(UserRole.USER);
        String storedToken = "stored-token";
        String requestToken = "request-token";

        given(refreshTokenRepository.findByUserId(userId)).willReturn(Optional.of(storedToken));

        assertThatThrownBy(() -> tokenService.refresh(userId, roles, requestToken))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_INVALID_REFRESH_TOKEN.name());
    }

    @Test
    @DisplayName("extractUserIdFromRefreshToken() - 정상 반환")
    void extractUserIdFromRefreshToken_success() {
        String refreshToken = "refresh-token";
        Long userId = 1L;

        given(jwtTokenProvider.getUserId(refreshToken)).willReturn(userId);

        Long result = tokenService.extractUserIdFromRefreshToken(refreshToken);

        assertThat(result).isEqualTo(userId);
    }
}
