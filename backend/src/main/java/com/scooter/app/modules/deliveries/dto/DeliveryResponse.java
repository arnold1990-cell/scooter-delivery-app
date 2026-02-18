package com.scooter.app.modules.deliveries.dto;

import com.scooter.app.modules.deliveries.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryResponse {
    private UUID id;
    private UUID customerId;
    private UUID riderId;
    private String pickupAddress;
    private String dropoffAddress;
    private BigDecimal price;
    private DeliveryStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
