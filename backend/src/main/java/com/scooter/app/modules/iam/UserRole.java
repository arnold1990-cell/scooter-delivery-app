package com.scooter.app.modules.iam;

import java.util.Arrays;
import java.util.Locale;

public enum UserRole {
    CUSTOMER,
    RIDER,
    ADMIN;

    public static UserRole from(String rawRole) {
        if (rawRole == null || rawRole.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }

        String normalized = rawRole.trim().toUpperCase(Locale.ROOT);
        if (normalized.startsWith("ROLE_")) {
            normalized = normalized.substring("ROLE_".length());
        }

        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid role '%s'. Allowed roles: %s".formatted(
                                rawRole,
                                Arrays.toString(values()).toLowerCase(Locale.ROOT)
                        )
                ));
    }
}
