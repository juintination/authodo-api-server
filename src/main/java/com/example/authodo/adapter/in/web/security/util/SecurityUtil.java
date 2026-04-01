package com.example.authodo.adapter.in.web.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    public UserDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user in SecurityContext");
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof UserDetails userDetails)) {
            throw new IllegalStateException("Principal is not of type UserDetails");
        }

        return userDetails;
    }

    public Long getCurrentUserId() {
        return Long.valueOf(getCurrentUser().getUsername());
    }
}
