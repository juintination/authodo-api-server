package com.example.authodo.adapter.out.redis.key;

public final class RedisKeys {

    private static final String AUTH_PREFIX = "auth:";
    private static final String REFRESH_TOKEN = "refresh-token:";

    private RedisKeys() {
    }

    public static String refreshToken(Long userId) {
        return AUTH_PREFIX + REFRESH_TOKEN + userId;
    }
}
