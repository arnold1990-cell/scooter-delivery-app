package com.scooter.app.modules.deliveries.dto;

import com.scooter.app.modules.deliveries.ChangedByRole;
import com.scooter.app.modules.deliveries.DeliveryStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class DeliveryStatusHistoryResponse {
    private UUID id;
    private DeliveryStatus status;
    private UUID changedByUserId;
    private ChangedByRole changedByRole;
    private String notes;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime createdAt;
}
