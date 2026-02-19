package com.scooter.app.modules.deliveries.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class DeliveryEtaResponse {
    private BigDecimal distanceKm;
    private LocalDateTime estimatedPickupTime;
    private LocalDateTime estimatedDeliveryTime;
}
