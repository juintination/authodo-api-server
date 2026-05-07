package com.example.authodo.application.auth.usecase.signup;

import com.example.authodo.application.auth.service.TokenService;
import com.example.authodo.application.auth.dto.command.SignupCommand;
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
public class SignupService implements SignupUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public TokenResult signup(SignupCommand command) {
        if (userRepositoryPort.existsByEmail(command.email())) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, command.email());
        }
        if (userRepositoryPort.existsByNickname(command.nickname())) {
            throw new BusinessException(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS, command.nickname());
        }

        String encoded = passwordEncoder.encode(command.password());
        User user = userRepositoryPort.save(User.create(command.email(), encoded, command.nickname()));

        return tokenService.issue(user.getId(), user.getRoles());
    }
}
