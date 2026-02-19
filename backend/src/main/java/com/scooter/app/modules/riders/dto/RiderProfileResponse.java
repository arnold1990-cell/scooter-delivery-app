package com.scooter.app.modules.riders.dto;

import com.scooter.app.modules.riders.ApprovalStatus;
import com.scooter.app.modules.riders.RiderStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class RiderProfileResponse {
    private UUID id;
    private UUID userId;
    private String fullName;
    private String email;
    private String licenseNumber;
    private ApprovalStatus approvalStatus;
    private Boolean isOnline;
    private RiderStatus status;
}
