package com.example.authodo.application.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.common.error.BusinessException;
import com.example.authodo.common.error.ErrorCode;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.out.UserRepositoryPort;
import java.util.Optional;
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

    @InjectMocks
    private AuthService authService;

    private User sampleUser(Long id, String email, String password, String nickname) {
        return User.create(email, password, nickname).toBuilder().id(id).build();
    }

    @Test
    @DisplayName("signup() - 이메일 중복 예외")
    void signup_emailExists() {
        given(userRepositoryPort.existsByEmail("test@test.com")).willReturn(true);

        assertThatThrownBy(() -> authService.signup("test@test.com", "pw", "nick"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS.name());

        verify(userRepositoryPort).existsByEmail("test@test.com");
    }

    @Test
    @DisplayName("signup() - 닉네임 중복 예외")
    void signup_nicknameExists() {
        given(userRepositoryPort.existsByEmail("test@test.com")).willReturn(false);
        given(userRepositoryPort.existsByNickname("nick")).willReturn(true);

        assertThatThrownBy(() -> authService.signup("test@test.com", "pw", "nick"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS.name());

        verify(userRepositoryPort).existsByNickname("nick");
    }

    @Test
    @DisplayName("signup() - 정상 생성")
    void signup_success() {
        given(userRepositoryPort.existsByEmail("test@test.com")).willReturn(false);
        given(userRepositoryPort.existsByNickname("nick")).willReturn(false);
        given(passwordEncoder.encode("pw")).willReturn("encodedPw");

        User savedUser = sampleUser(1L, "test@test.com", "encodedPw", "nick");
        given(userRepositoryPort.save(any(User.class))).willReturn(savedUser);

        User result = authService.signup("test@test.com", "pw", "nick");

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getEmail()).isEqualTo("test@test.com");
        assertThat(result.getPassword()).isEqualTo("encodedPw");
        assertThat(result.getNickname()).isEqualTo("nick");
        verify(userRepositoryPort).save(any(User.class));
    }

    @Test
    @DisplayName("login() - 이메일 없음 예외")
    void login_emailNotFound() {
        given(userRepositoryPort.findByEmail("test@test.com")).willReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login("test@test.com", "pw"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL.name());
    }

    @Test
    @DisplayName("login() - 비밀번호 불일치 예외")
    void login_invalidPassword() {
        User user = sampleUser(1L, "test@test.com", "encodedPw", "nick");
        given(userRepositoryPort.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("pw", "encodedPw")).willReturn(false);

        assertThatThrownBy(() -> authService.login("test@test.com", "pw"))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_INVALID_PASSWORD.name());
    }

    @Test
    @DisplayName("login() - 정상 로그인")
    void login_success() {
        User user = sampleUser(1L, "test@test.com", "encodedPw", "nick");
        given(userRepositoryPort.findByEmail("test@test.com")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("pw", "encodedPw")).willReturn(true);

        User result = authService.login("test@test.com", "pw");

        assertThat(result).isEqualTo(user);
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
        User user = sampleUser(1L, "test@test.com", "encodedPw", "nick");
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));

        User result = authService.getById(1L);

        assertThat(result).isEqualTo(user);
    }
}
