package com.scooter.app.modules.pricing.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingConfigRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal baseFare;
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal perKmRate;
    @NotNull
    @DecimalMin("0.1")
    private BigDecimal peakMultiplier;
}
