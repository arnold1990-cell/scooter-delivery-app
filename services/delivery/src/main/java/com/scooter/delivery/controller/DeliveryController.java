package com.scooter.delivery.controller;

import com.scooter.delivery.dto.CreateDeliveryRequest;
import com.scooter.delivery.dto.DeliveryResponse;
import com.scooter.delivery.entity.DeliveryStatus;
import com.scooter.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deliveries")
public class DeliveryController {

    private final DeliveryService service;

    public DeliveryController(DeliveryService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> create(
            @RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey,
            @Valid @RequestBody CreateDeliveryRequest request,
            Principal principal) {
        var delivery = service.createDelivery(principal.getName(), request, idempotencyKey);
        return ResponseEntity.ok(new DeliveryResponse(delivery.getId(), delivery.getStatus(), delivery.getCreatedAt()));
    }

    @PostMapping("/{deliveryId}/status/{status}")
    @PreAuthorize("hasRole('RIDER') or hasRole('ADMIN')")
    public ResponseEntity<DeliveryResponse> transition(@PathVariable UUID deliveryId, @PathVariable DeliveryStatus status) {
        var delivery = service.transition(deliveryId, status);
        return ResponseEntity.ok(new DeliveryResponse(delivery.getId(), delivery.getStatus(), delivery.getCreatedAt()));
    }
}
