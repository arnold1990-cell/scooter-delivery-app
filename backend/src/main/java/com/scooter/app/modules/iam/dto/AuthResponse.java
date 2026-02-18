package com.scooter.app.modules.iam.dto;

import com.scooter.app.modules.iam.UserRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private UserRole role;
}
