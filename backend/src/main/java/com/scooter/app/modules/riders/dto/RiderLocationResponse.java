package com.scooter.app.modules.riders.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RiderLocationResponse {
    private UUID riderId;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime updatedAt;
}
