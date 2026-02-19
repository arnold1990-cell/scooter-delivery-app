package com.scooter.app.modules.customers.dto;

import com.scooter.app.modules.deliveries.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CustomerDeliveryHistoryItem {
    private UUID id;
    private String pickupAddress;
    private String dropoffAddress;
    private BigDecimal price;
    private DeliveryStatus latestStatus;
    private LocalDateTime latestStatusAt;
    private LocalDateTime createdAt;
}
