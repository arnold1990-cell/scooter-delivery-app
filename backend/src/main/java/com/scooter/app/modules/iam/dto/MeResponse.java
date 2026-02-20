package com.scooter.app.modules.iam.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MeResponse {
    private UUID userId;
    private String email;
    private List<String> roles;
}
