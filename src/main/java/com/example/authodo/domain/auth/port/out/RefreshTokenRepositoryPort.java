package com.example.authodo.domain.auth.port.out;

import java.util.Optional;

public interface RefreshTokenRepositoryPort {

    void save(Long userId, String refreshToken, long ttl);

    Optional<String> findByUserId(Long userId);

    void delete(Long userId);
}
