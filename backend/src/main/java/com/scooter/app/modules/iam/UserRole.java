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

        return Arrays.stream(values())
                .filter(role -> role.name().equalsIgnoreCase(rawRole.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Invalid role '%s'. Allowed roles: %s".formatted(
                                rawRole,
                                Arrays.toString(values()).toLowerCase(Locale.ROOT)
                        )
                ));
    }
}
