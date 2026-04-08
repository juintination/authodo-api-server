package com.example.authodo.application.auth;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.adapter.in.web.security.jwt.JwtTokenProvider;
import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.command.SignupCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.auth.port.in.AuthUseCasePort;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AuthService implements AuthUseCasePort {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public TokenResult signup(SignupCommand command) {
        String email = command.email();
        String nickname = command.nickname();
        String password = command.password();

        if (userRepositoryPort.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, email);
        }
        if (userRepositoryPort.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS, nickname);
        }

        String encoded = passwordEncoder.encode(password);
        User user = userRepositoryPort.save(
            User.create(email, encoded, nickname)
        );

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), List.of());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new TokenResult(accessToken, refreshToken);
    }

    @Override
    public TokenResult login(LoginCommand command) {
        String email = command.email();
        String password = command.password();

        User user = userRepositoryPort.findByEmail(email)
            .orElseThrow(() ->
                new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL, email)
            );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), List.of());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        return new TokenResult(accessToken, refreshToken);
    }

    @Override
    public User getById(Long id) {
        return userRepositoryPort.findById(id).orElseThrow(() ->
            new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_ID, id)
        );
    }
}
