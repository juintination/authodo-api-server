package com.example.authodo.adapter.out.redis.repository;

import com.example.authodo.adapter.out.redis.key.RedisKeys;
import com.example.authodo.domain.auth.port.out.RefreshTokenRepositoryPort;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository implements RefreshTokenRepositoryPort {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void save(Long userId, String refreshToken, long ttl) {
        redisTemplate.opsForValue().set(
            RedisKeys.refreshToken(userId),
            refreshToken,
            java.time.Duration.ofMillis(ttl)
        );
    }

    @Override
    public Optional<String> findByUserId(Long userId) {
        return Optional.ofNullable(
            redisTemplate.opsForValue().get(RedisKeys.refreshToken(userId))
        );
    }

    @Override
    public void delete(Long userId) {
        redisTemplate.delete(RedisKeys.refreshToken(userId));
    }
}
