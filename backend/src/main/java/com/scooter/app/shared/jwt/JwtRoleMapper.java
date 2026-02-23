package com.scooter.app.shared.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Component
public class JwtRoleMapper {

    private static final String ROLE_PREFIX = "ROLE_";

    public Collection<? extends GrantedAuthority> toAuthorities(List<String> roles) {
        if (roles == null || roles.isEmpty()) {
            return Collections.emptyList();
        }

        return roles.stream()
                .filter(role -> role != null && !role.isBlank())
                .map(this::toAuthority)
                .map(SimpleGrantedAuthority::new)
                .toList();
    }

    public String normalizeRole(String role) {
        if (role == null || role.isBlank()) {
            return "CUSTOMER";
        }

        String trimmed = role.trim();
        return trimmed.startsWith(ROLE_PREFIX) ? trimmed.substring(ROLE_PREFIX.length()) : trimmed;
    }

    public String toAuthority(String role) {
        String normalizedRole = normalizeRole(role).toUpperCase();
        return ROLE_PREFIX + normalizedRole;
    }
}
