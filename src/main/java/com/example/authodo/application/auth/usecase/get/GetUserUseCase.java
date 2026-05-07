package com.example.authodo.application.auth.usecase.get;

import com.example.authodo.domain.user.User;

public interface GetUserUseCase {

    User getById(Long id);
}
