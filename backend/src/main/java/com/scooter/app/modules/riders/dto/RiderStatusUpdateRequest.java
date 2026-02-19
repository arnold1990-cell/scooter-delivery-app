package com.scooter.app.modules.riders.dto;

import com.scooter.app.modules.riders.RiderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RiderStatusUpdateRequest {
    @NotNull
    private RiderStatus status;
}
