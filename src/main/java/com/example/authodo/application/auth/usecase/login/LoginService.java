package com.example.authodo.application.auth.usecase.login;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.application.common.error.ErrorCode;
import com.example.authodo.application.common.exception.BusinessException;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoginService implements LoginUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional(readOnly = true)
    public TokenResult login(LoginCommand command) {
        User user = userRepositoryPort.findByEmail(command.email())
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL, command.email()));

        if (!passwordEncoder.matches(command.password(), user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }

        return tokenService.issue(user.getId(), user.getRoles());
    }
}
