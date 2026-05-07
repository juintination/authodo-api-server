package com.example.authodo.application.auth.usecase.get;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

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

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserService 단위 테스트")
class GetUserServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;

    @InjectMocks
    private GetUserService getUserService;

    @Test
    @DisplayName("존재하지 않으면 예외")
    void getById_notFound() {
        given(userRepositoryPort.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> getUserService.getById(1L))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining(ErrorCode.AUTH_USER_NOT_FOUND_ID.name());
    }

    @Test
    @DisplayName("정상 반환")
    void getById_success() {
        Set<UserRole> roles = Set.of(UserRole.USER);
        User user = User.create("test@test.com", "encodedPw", "nick")
            .toBuilder().id(1L).roles(roles).build();
        given(userRepositoryPort.findById(1L)).willReturn(Optional.of(user));

        User result = getUserService.getById(1L);

        assertThat(result).isEqualTo(user);
    }
}
