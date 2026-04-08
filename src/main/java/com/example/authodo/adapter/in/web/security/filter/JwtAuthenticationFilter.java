package com.example.authodo.adapter.in.web.security.filter;

import com.example.authodo.adapter.in.web.security.exception.JwtAuthenticationException;
import com.example.authodo.adapter.in.web.security.handler.JwtAuthenticationEntryPoint;
import com.example.authodo.adapter.in.web.security.jwt.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String[] publicEndpoints = {
        "/docs/**",
        "/api/auth/login",
        "/api/auth/signup",
        "/api/auth/refresh"
    };

    private final JwtTokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        String path = request.getRequestURI();
        for (String pattern : publicEndpoints) {
            if (path.startsWith(pattern.replace("/**", ""))) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String token = resolveToken(request);

        try {
            if (token != null) {
                Authentication auth = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);

        } catch (JwtAuthenticationException e) {
            SecurityContextHolder.clearContext();
            authenticationEntryPoint.commence(request, response, e);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        return (bearer != null && bearer.startsWith(BEARER_PREFIX))
            ? bearer.substring(BEARER_PREFIX.length())
            : null;
    }
}
