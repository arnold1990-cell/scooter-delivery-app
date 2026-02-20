package com.scooter.delivery.events;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryEvent(UUID deliveryId, String customerId, String pickupAddress, String dropoffAddress, String status, OffsetDateTime at) {}
