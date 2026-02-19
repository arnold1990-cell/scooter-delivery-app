package com.scooter.app.modules.deliveries.dto;

import com.scooter.app.modules.deliveries.DeliveryStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateStatusRequest {
    @NotNull
    private DeliveryStatus status;
    private String notes;
    private BigDecimal latitude;
    private BigDecimal longitude;
}
