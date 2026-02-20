package com.scooter.tracking.controller;

import com.scooter.tracking.dto.LocationPingRequest;
import com.scooter.tracking.events.RiderLocationUpdatedEvent;
import com.scooter.tracking.service.TrackingService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class TrackingController {

    private final TrackingService trackingService;

    public TrackingController(TrackingService trackingService) {
        this.trackingService = trackingService;
    }

    @PostMapping("/api/tracking/pings")
    @PreAuthorize("hasRole('RIDER')")
    public RiderLocationUpdatedEvent ping(@Valid @RequestBody LocationPingRequest request) {
        return trackingService.upsertLocation(request);
    }

    @GetMapping("/ws/deliveries/{deliveryId}/stream")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public SseEmitter stream(@PathVariable String deliveryId) {
        return trackingService.subscribe(deliveryId);
    }
}
