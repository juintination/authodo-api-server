package com.example.authodo.adapter.in.web.security.jwt;

import com.example.authodo.adapter.in.web.common.error.ErrorCode;
import com.example.authodo.adapter.in.web.security.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long accessExpirationMs;
    private final long refreshExpirationMs;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.access.expiration-ms}") long accessExpirationMs,
        @Value("${jwt.refresh.expiration-ms}") long refreshExpirationMs
    ) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException(
                "JWT secret must be at least 32 bytes, but was " + keyBytes.length + " bytes"
            );
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationMs = accessExpirationMs;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String createAccessToken(Long userId, List<String> roles) {
        return createToken(userId, roles, accessExpirationMs);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, List.of(), refreshExpirationMs);
    }

    private String createToken(Long userId, List<String> roles, long exp) {
        long nowMs = System.currentTimeMillis();

        JwtBuilder builder = Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(new Date(nowMs))
            .expiration(new Date(nowMs + exp))
            .signWith(key);

        if (!roles.isEmpty()) {
            builder.claim("roles", roles);
        }

        return builder.compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        Long userId = Long.valueOf(claims.getSubject());

        List<?> rawRoles = claims.get("roles", List.class);
        List<SimpleGrantedAuthority> authorities = rawRoles == null
            ? List.of()
            : rawRoles.stream()
                .map(role -> "ROLE_" + role.toString())
                .map(SimpleGrantedAuthority::new)
                .toList();

        UserDetails userDetails = User.withUsername(String.valueOf(userId))
            .password("")
            .authorities(authorities)
            .build();

        return new UsernamePasswordAuthenticationToken(userDetails, token, authorities);
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        } catch (ExpiredJwtException e) {
            throw new JwtAuthenticationException(ErrorCode.TOKEN_EXPIRED, e);
        } catch (JwtException e) {
            throw new JwtAuthenticationException(ErrorCode.INVALID_TOKEN, e);
        }
    }

    public Long getUserId(String token) {
        Claims claims = parseClaims(token);
        return Long.valueOf(claims.getSubject());
    }

    public long getRefreshExpirationMs() {
        return refreshExpirationMs;
    }
}
