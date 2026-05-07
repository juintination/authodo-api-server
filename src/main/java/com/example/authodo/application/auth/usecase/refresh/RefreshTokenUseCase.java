package com.example.authodo.application.auth.usecase.refresh;

import com.example.authodo.application.auth.dto.command.RefreshTokenCommand;
import com.example.authodo.application.auth.dto.result.TokenResult;

public interface RefreshTokenUseCase {

    TokenResult refresh(RefreshTokenCommand command);
}
