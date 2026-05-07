package com.example.authodo.application.auth.port.out;

import java.util.List;

public interface TokenProviderPort {

    String createAccessToken(Long userId, List<String> roles);

    String createRefreshToken(Long userId);

    Long getUserId(String token);

    long getRefreshExpirationMs();
}
