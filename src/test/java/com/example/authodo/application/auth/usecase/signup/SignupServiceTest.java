package com.example.authodo.application.auth.usecase.signup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.SignupCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.application.common.error.ErrorCode;
import com.example.authodo.application.common.exception.BusinessException;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.enums.UserRole;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("SignupService 단위 테스트")
class SignupServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private SignupService signupService;

    @Test
    @DisplayName("이메일 중복 예외")
    void signup_emailExists() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");
        given(userRepositoryPort.existsByEmail(command.email())).willReturn(true);

        assertThatThrownBy(() -> signupService.signup(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS.name());
    }

    @Test
    @DisplayName("닉네임 중복 예외")
    void signup_nicknameExists() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");
        given(userRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(userRepositoryPort.existsByNickname(command.nickname())).willReturn(true);

        assertThatThrownBy(() -> signupService.signup(command))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS.name());
    }

    @Test
    @DisplayName("정상 회원가입")
    void signup_success() {
        SignupCommand command = new SignupCommand("test@test.com", "pw", "nick");
        Set<UserRole> roles = Set.of(UserRole.USER);
        User savedUser = User.create(command.email(), "encodedPw", command.nickname())
            .toBuilder().id(1L).roles(roles).build();

        given(userRepositoryPort.existsByEmail(command.email())).willReturn(false);
        given(userRepositoryPort.existsByNickname(command.nickname())).willReturn(false);
        given(passwordEncoder.encode(command.password())).willReturn("encodedPw");
        given(userRepositoryPort.save(any(User.class))).willReturn(savedUser);
        given(tokenService.issue(1L, roles)).willReturn(new TokenResult("access-token", "refresh-token"));

        TokenResult result = signupService.signup(command);

        assertThat(result.accessToken()).isEqualTo("access-token");
        assertThat(result.refreshToken()).isEqualTo("refresh-token");
        verify(userRepositoryPort).save(any(User.class));
    }
}
