package com.example.authodo.application.auth.usecase.get;

import com.example.authodo.application.common.error.ErrorCode;
import com.example.authodo.application.common.exception.BusinessException;
import com.example.authodo.domain.user.User;
import com.example.authodo.domain.user.port.out.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetUserService implements GetUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepositoryPort.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_USER_NOT_FOUND_ID, id));
    }
}
