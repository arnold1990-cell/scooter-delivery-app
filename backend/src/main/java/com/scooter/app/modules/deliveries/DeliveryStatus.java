package com.scooter.app.modules.deliveries;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

public enum DeliveryStatus {
    PENDING,
    ASSIGNED,
    PICKED_UP,
    IN_TRANSIT,
    DELIVERED,
    CANCELLED,
    FAILED;

    public static DeliveryStatus from(String rawStatus) {
        if (rawStatus == null || rawStatus.isBlank()) {
            throw new IllegalArgumentException("status must not be blank");
        }

        String normalized = rawStatus.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "REQUESTED" -> PENDING;
            case "REJECTED" -> FAILED;
            case "ACCEPTED" -> ASSIGNED;
            default -> {
                try {
                    yield DeliveryStatus.valueOf(normalized);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Invalid status '" + rawStatus + "'. Allowed values: " + allowedValues());
                }
            }
        };
    }

    public static String allowedValues() {
        return Arrays.stream(values())
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
