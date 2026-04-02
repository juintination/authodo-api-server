package com.example.authodo.application.auth;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.common.exception.BusinessException;
import com.example.authodo.domain.auth.port.in.AuthUseCasePort;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService implements AuthUseCasePort {

    private final UserRepositoryPort userRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User signup(String email, String password, String nickname) {
        if (userRepositoryPort.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.AUTH_EMAIL_ALREADY_EXISTS, email);
        }
        if (userRepositoryPort.existsByNickname(nickname)) {
            throw new BusinessException(ErrorCode.AUTH_NICKNAME_ALREADY_EXISTS, nickname);
        }

        String encoded = passwordEncoder.encode(password);

        User user = User.create(email, encoded, nickname);

        return userRepositoryPort.save(user);
    }

    @Override
    public User login(String email, String password) {
        User user = userRepositoryPort.findByEmail(email).orElseThrow(() ->
            new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_EMAIL, email)
        );

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BusinessException(ErrorCode.AUTH_INVALID_PASSWORD);
        }

        return user;
    }

    @Override
    public User getById(Long id) {
        return userRepositoryPort.findById(id).orElseThrow(() ->
            new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_ID, id)
        );
    }
}
