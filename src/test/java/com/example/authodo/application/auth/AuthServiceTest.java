package com.example.authodo.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.command.RefreshTokenCommand;
import com.example.authodo.application.auth.dto.command.SignupCommand;
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
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser(Long id, String email, String password, String nickname, Set<UserRole> roles) {
        return User.create(email, password, nickname)
            .toBuilder()
            .id(id)
            .roles(roles)
            .build();
    }

    @Test
    @DisplayName("signup() - 이메일 중복 예외")
    void signup_emailExists() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");

        given(userRepositoryPort.existsByEmail(command.email())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS.name());

        verify(userRepositoryPort).existsByEmail(command.email());
    }

    @Test
    @DisplayName("signup() - 닉네임 중복 예외")
    void signup_nicknameExists() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");

        given(userRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(userRepositoryPort.existsByNickname(command.nickname())).willReturn(true);

        assertThatThrownBy(() -> authService.signup(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS.name());

        verify(userRepositoryPort).existsByNickname(command.nickname());
    }

    @Test
    @DisplayName("signup() - 정상 생성")
    void signup_success() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");

        given(userRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(userRepositoryPort.existsByNickname(command.nickname())).willReturn(false);
        given(passwordEncoder.encode(command.password())).willReturn("encodedPw");

        Set<UserRole> roles = Set.of(UserRole.USER);
        User savedUser = sampleUser(1L, command.email(), "encodedPw", command.nickname(), roles);
        given(userRepositoryPort.save(any(User.class))).willReturn(savedUser);

        given(tokenService.issue(1L, roles))
            .willReturn(new TokenResult("access-token", "refresh-token"));

        TokenResult result = authService.signup(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        verify(userRepositoryPort).save(any(User.class));
        verify(tokenService).issue(1L, roles);
    }

    @Test
    @DisplayName("login() - 이메일 없음 예외")
    void login_emailNotFound() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");

        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL.name());
    }

    @Test
    @DisplayName("login() - 비밀번호 불일치 예외")
    void login_invalidPassword() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");

        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = sampleUser(1L, command.email(), "encodedPw", "nick", roles);

        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(false);

        assertThatThrownBy(() -> authService.login(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_INVALID_PASSWORD.name());
    }

    @Test
    @DisplayName("login() - 정상 로그인")
    void login_success() {
        LoginCommand command = new LoginCommand("test@test.com", "pw");

        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = sampleUser(1L, command.email(), "encodedPw", "nick", roles);

        given(userRepositoryPort.findByEmail(command.email())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(command.password(), user.getPassword())).willReturn(true);

        given(tokenService.issue(1L, roles))
            .willReturn(new TokenResult("access-token", "refresh-token"));

        TokenResult result = authService.login(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");

        verify(tokenService).issue(1L, roles);
    }

    @Test
    @DisplayName("refresh() - 정상 갱신")
    void refresh_success() {
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = sampleUser(1L, "test@test.com", "encodedPw", "nick", roles);

        RefreshTokenCommand command = new RefreshTokenCommand("refresh-token");

        given(tokenService.extractUserIdFromRefreshToken("refresh-token")).willReturn(1L);
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));
        given(tokenService.refresh(1L, roles, "refresh-token"))
            .willReturn(new TokenResult("new-access-token", "new-refresh-token"));

        TokenResult result = authService.refresh(command);

        assertThat(result.accessToken()).isEqualTo("new-access-token");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-token");

        verify(tokenService).extractUserIdFromRefreshToken("refresh-token");
        verify(tokenService).refresh(1L, roles, "refresh-token");
    }

    @Test
    @DisplayName("getById() - 존재하지 않으면 예외")
    void getById_notFound() {
        given(userRepositoryPort.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.getById(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_USER_NOT_FOUND_ID.name());
    }

    @Test
    @DisplayName("getById() - 정상 반환")
    void getById_success() {
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = sampleUser(1L, "test@test.com", "encodedPw", "nick", roles);

        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));

        User result = authService.getById(1L);

        assertThat(result).isEqualTo(user);
    }
}
