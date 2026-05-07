package com.example.authodo.application.auth.usecase.signup;

import com.example.authodo.application.auth.dto.command.SignupCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;

public interface SignupUseCase {

    TokenResult signup(SignupCommand command);
}
