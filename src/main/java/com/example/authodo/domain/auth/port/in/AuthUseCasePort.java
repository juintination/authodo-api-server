package com.example.authodo.domain.auth.port.in;

import com.example.authodo.domain.user.User;

public interface AuthUseCasePort {

    User signup(String email, String password, String nickname);

    User login(String email, String password);

    User getById(Long id);
}
