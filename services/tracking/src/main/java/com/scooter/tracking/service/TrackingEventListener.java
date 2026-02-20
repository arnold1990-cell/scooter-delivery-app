package com.scooter.tracking.service;

import com.scooter.tracking.events.RiderLocationUpdatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TrackingEventListener {

    private final TrackingService trackingService;

    public TrackingEventListener(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @KafkaListener(topics = "rider.location.updated", groupId = "tracking-service")
    public void listen(RiderLocationUpdatedEvent event) {
        trackingService.broadcast(event);
    }
}
