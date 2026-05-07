package com.example.authodo.application.auth.usecase.login;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.application.common.error.ErrorCode;
import com.example.authodo.application.common.exception.BusinessException;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("LoginService 단위 테스트")
class LoginServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private LoginService loginService;

    @Test
    @DisplayName("이메일 없음 예외")
    void login_emailNotFound() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");
        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.empty());

        assertThatThrownBy(() -> loginService.login(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL.name());
    }

    @Test
    @DisplayName("비밀번호 불일치 예외")
    void login_invalidPassword() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = User.create(command.email(), "encodedPw", "nick")
            .toBuilder().id(1L).roles(roles).build();

        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> loginService.login(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_INVALID_PASSWORD.name());
    }

    @Test
    @DisplayName("정상 로그인")
    void login_success() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = User.create(command.email(), "encodedPw", "nick")
            .toBuilder().id(1L).roles(roles).build();

        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(true);
        given(tokenService.issue(1L, roles)).willReturn(new TokenResult("access-token", "refresh-token"));

        TokenResult result = loginService.login(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        verify(tokenService).issue(1L, roles);
    }
}
