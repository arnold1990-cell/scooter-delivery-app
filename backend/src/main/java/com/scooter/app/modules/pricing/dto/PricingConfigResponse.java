package com.scooter.app.modules.pricing.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PricingConfigResponse {
    private BigDecimal baseFare;
    private BigDecimal perKmRate;
    private BigDecimal peakMultiplier;
    private LocalDateTime updatedAt;
}
