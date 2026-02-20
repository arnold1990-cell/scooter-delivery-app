package com.scooter.dispatch.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryCreatedEvent(UUID deliveryId, String customerId, String pickupAddress, String dropoffAddress, String status, OffsetDateTime at) {}
