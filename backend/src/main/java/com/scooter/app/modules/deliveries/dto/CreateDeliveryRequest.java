package com.scooter.app.modules.deliveries.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDeliveryRequest {
    @NotBlank
    private String pickupAddress;
    @NotBlank
    private String dropoffAddress;
    @DecimalMin(value = "0.0")
    private BigDecimal price;
    private String notes;
    private String status;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal pickupLatitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal pickupLongitude;

    @NotNull
    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal dropoffLatitude;

    @NotNull
    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal dropoffLongitude;
}
