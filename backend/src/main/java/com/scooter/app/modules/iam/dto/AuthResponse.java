package com.scooter.app.modules.iam.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private List<String> roles;
    private String accessToken;
    private String refreshToken;
    /**
     * Backward-compatible token field retained for existing frontend calls.
     */
    private String token;
}
