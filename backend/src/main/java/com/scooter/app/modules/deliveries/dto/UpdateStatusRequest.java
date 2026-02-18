package com.scooter.app.modules.deliveries.dto;

import com.scooter.app.modules.deliveries.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {
    @NotNull
    private DeliveryStatus status;
}
