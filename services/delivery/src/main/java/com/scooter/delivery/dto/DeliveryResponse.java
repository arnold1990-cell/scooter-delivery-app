package com.scooter.delivery.dto;

import com.scooter.delivery.entity.DeliveryStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryResponse(UUID id, DeliveryStatus status, OffsetDateTime createdAt) {}
