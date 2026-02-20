package com.scooter.tracking.events;

import java.time.OffsetDateTime;

public record RiderLocationUpdatedEvent(
        String deliveryId,
        String riderId,
        double latitude,
        double longitude,
        double speedKph,
        int etaSeconds,
        OffsetDateTime at
) {}
