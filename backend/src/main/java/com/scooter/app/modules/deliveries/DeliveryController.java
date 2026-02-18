package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.deliveries.dto.CreateDeliveryRequest;
import com.scooter.app.modules.deliveries.dto.DeliveryResponse;
import com.scooter.app.modules.deliveries.dto.UpdateStatusRequest;
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
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PostMapping("/api/deliveries")
    @PreAuthorize("hasRole('CUSTOMER')")
    public DeliveryResponse create(Authentication authentication, @Valid @RequestBody CreateDeliveryRequest request) {
        return deliveryService.create(authentication.getName(), request);
    }

    @GetMapping("/api/deliveries/my")
    @PreAuthorize("hasRole('CUSTOMER')")
    public List<DeliveryResponse> my(Authentication authentication) {
        return deliveryService.customerDeliveries(authentication.getName());
    }

    @GetMapping("/api/rider/jobs")
    @PreAuthorize("hasRole('RIDER')")
    public List<DeliveryResponse> jobs() {
        return deliveryService.availableJobs();
    }

    @GetMapping("/api/rider/active")
    @PreAuthorize("hasRole('RIDER')")
    public List<DeliveryResponse> active(Authentication authentication) {
        return deliveryService.riderDeliveries(authentication.getName());
    }

    @PatchMapping("/api/deliveries/{id}/status")
    @PreAuthorize("hasRole('RIDER')")
    public DeliveryResponse updateStatus(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody UpdateStatusRequest request) {
        return deliveryService.updateStatus(authentication.getName(), id, request.getStatus());
    }

    @GetMapping("/api/admin/deliveries")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<DeliveryResponse> all(@RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
        return deliveryService.adminAll(page, size);
    }
}
