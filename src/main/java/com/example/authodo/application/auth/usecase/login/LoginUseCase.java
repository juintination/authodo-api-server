package com.example.authodo.application.auth.usecase.login;

import com.example.authodo.application.auth.dto.command.LoginCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;

public interface LoginUseCase {

    TokenResult login(LoginCommand command);
}
