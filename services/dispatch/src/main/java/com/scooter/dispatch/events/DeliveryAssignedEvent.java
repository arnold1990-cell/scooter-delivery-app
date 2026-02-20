package com.scooter.dispatch.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryAssignedEvent(UUID deliveryId, String riderId, double score, OffsetDateTime at) {}
