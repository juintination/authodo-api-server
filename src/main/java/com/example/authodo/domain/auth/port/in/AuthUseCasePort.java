package com.example.authodo.domain.auth.port.in;

import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.command.SignupCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;
import com.example.authodo.domain.user.User;

public interface AuthUseCasePort {

    TokenResult signup(SignupCommand command);

    TokenResult login(LoginCommand command);

    User getById(Long id);
}
