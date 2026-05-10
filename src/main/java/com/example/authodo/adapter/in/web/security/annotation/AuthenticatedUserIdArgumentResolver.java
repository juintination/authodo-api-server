package com.example.authodo.adapter.in.web.security.annotation;

import com.example.authodo.adapter.in.web.security.exception.JwtAuthenticationException;
import com.example.authodo.application.common.error.ErrorCode;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthenticatedUserIdArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(
        MethodParameter parameter
    ) {
        return parameter.hasParameterAnnotation(AuthenticatedUserId.class)
            && parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new JwtAuthenticationException(ErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails userDetails)) {
            throw new JwtAuthenticationException(ErrorCode.UNAUTHORIZED);
        }

        return Long.valueOf(userDetails.getUsername());
    }
}
