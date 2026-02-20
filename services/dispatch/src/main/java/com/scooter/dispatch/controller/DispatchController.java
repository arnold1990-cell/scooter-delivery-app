package com.scooter.dispatch.controller;

import com.scooter.dispatch.dto.RiderScoreRequest;
import com.scooter.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dispatch")
public class DispatchController {

    private final DispatchService service;

    public DispatchController(DispatchService service) {
        this.service = service;
    }

    @PostMapping("/score")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> score(@Valid @RequestBody RiderScoreRequest request) {
        return Map.of("score", service.score(request));
    }

    @PostMapping("/deliveries/{deliveryId}/accept")
    @PreAuthorize("hasRole('RIDER')")
    public Map<String, Object> accept(@PathVariable UUID deliveryId, @RequestParam String riderId) {
        service.acceptOffer(deliveryId, riderId);
        return Map.of("status", "accepted", "deliveryId", deliveryId);
    }
}
