package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.deliveries.dto.*;
import com.scooter.app.modules.riders.dto.RiderLocationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/deliveries")
    @PreAuthorize("hasRole('CUSTOMER')")
    public DeliveryResponse create(Authentication authentication, @Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryService.create(authentication.getName(), request);
    }

    @GetMapping("/deliveries/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<DeliveryResponse> my(Authentication authentication) {
        return deliveryService.customerDeliveries(authentication.getName());
    }

    @GetMapping("/rider/jobs")
    @PreAuthorize("hasRole('RIDER')")
    public List<DeliveryResponse> jobs() {
        return deliveryService.availableJobs();
    }

    @GetMapping("/rider/active")
    @PreAuthorize("hasRole('RIDER')")
    public List<DeliveryResponse> active(Authentication authentication) {
        return deliveryService.riderDeliveries(authentication.getName());
    }

    @PostMapping("/deliveries/{id}/status")
    public DeliveryResponse updateStatus(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest request) {
        return deliveryService.updateStatus(authentication.getName(), ChangedByRole.RIDER, id, request);
    }

    @GetMapping("/deliveries/{id}/history")
    public List<DeliveryStatusHistoryResponse> history(@PathVariable UUID id) {
        return deliveryService.history(id);
    }

    @PostMapping("/deliveries/{id}/assign-nearest")
    public DeliveryResponse assignNearest(@PathVariable UUID id) {
        return deliveryService.assignNearest(id);
    }

    @GetMapping("/deliveries/{id}/rider-location")
    public RiderLocationResponse riderLocation(@PathVariable UUID id) {
        return deliveryService.riderLocation(id);
    }

    @GetMapping("/deliveries/{id}/eta")
    public DeliveryEtaResponse eta(@PathVariable UUID id) {
        return deliveryService.eta(id);
    }

    @GetMapping("/admin/deliveries")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<DeliveryResponse> all(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return deliveryService.adminAll(page, size);
    }
}
