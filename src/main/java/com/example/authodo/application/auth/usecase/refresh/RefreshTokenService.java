package com.example.authodo.application.auth.usecase.refresh;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.RefreshTokenCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.application.common.error.ErrorCode;
import com.example.authodo.application.common.exception.BusinessException;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import java.util.Set;
import com.example.authodo.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final TokenService tokenService;
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public TokenResult refresh(RefreshTokenCommand command) {
        String refreshToken = command.refreshToken();

        Long userId = tokenService.extractUserIdFromRefreshToken(refreshToken);

        User user = userRepositoryPort.findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_ID, userId));

        Set<UserRole> roles = user.getRoles();

        return tokenService.refresh(userId, roles, refreshToken);
    }
}
