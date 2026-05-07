package com.example.authodo.application.auth.usecase.refresh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.RefreshTokenCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.enums.UserRole;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("RefreshTokenService 단위 테스트")
class RefreshTokenServiceTest {

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    @Test
    @DisplayName("정상 토큰 갱신")
    void refresh_success() {
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = User.create("test@test.com", "encodedPw", "nick")
            .toBuilder().id(1L).roles(roles).build();
        RefreshTokenCommand command = new RefreshTokenCommand("refresh-token");

        given(tokenService.extractUserIdFromRefreshToken("refresh-token")).willReturn(1L);
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));
        given(tokenService.refresh(1L, roles, "refresh-token"))
            .willReturn(new TokenResult("new-access-token", "new-refresh-token"));

        TokenResult result = refreshTokenService.refresh(command);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");
        verify(tokenService).refresh(1L, roles, "refresh-token");
    }
}
