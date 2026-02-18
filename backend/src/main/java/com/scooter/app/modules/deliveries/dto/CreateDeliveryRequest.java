package com.scooter.app.modules.deliveries.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDeliveryRequest {
    @NotBlank
    private String pickupAddress;
    @NotBlank
    private String dropoffAddress;
    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;
    private String notes;
}
