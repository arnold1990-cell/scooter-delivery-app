package com.scooter.app.modules.riders;

import com.scooter.app.modules.riders.dto.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
public class RiderController {

    private final RiderService riderService;

    @GetMapping("/riders/me")
    @PreAuthorize("hasRole('RIDER')")
    public RiderProfileResponse me(Authentication authentication) {
        return riderService.getMe(authentication.getName());
    }

    @PatchMapping("/riders/me/online")
    @PreAuthorize("hasRole('RIDER')")
    public RiderProfileResponse toggleOnline(Authentication authentication, @Valid @RequestBody OnlineToggleRequest request) {
        return riderService.setOnline(authentication.getName(), request.getOnline());
    }

    @PutMapping("/riders/me/status")
    @PreAuthorize("hasRole('RIDER')")
    public RiderProfileResponse updateStatus(Authentication authentication, @Valid @RequestBody RiderStatusUpdateRequest request) {
        return riderService.setStatus(authentication.getName(), request.getStatus());
    }

    @PutMapping("/riders/me/location")
    @PreAuthorize("hasRole('RIDER')")
    public RiderLocationResponse updateLocation(Authentication authentication, @Valid @RequestBody RiderLocationUpdateRequest request) {
        return riderService.updateMyLocation(authentication.getName(), request);
    }

    @GetMapping("/riders/{riderId}/location")
    public RiderLocationResponse location(@PathVariable UUID riderId) {
        return riderService.getLocation(riderId);
    }

    @PatchMapping("/admin/riders/{userId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public RiderProfileResponse approve(@PathVariable UUID userId, @Valid @RequestBody ApprovalRequest request) {
        return riderService.approve(userId, request.getStatus());
    }

    @GetMapping("/admin/riders")
    @PreAuthorize("hasRole('ADMIN')")
    public List<RiderProfileResponse> allRiders() {
        return riderService.all();
    }

    @Data
    public static class ApprovalRequest {
        @NotNull
        private ApprovalStatus status;
    }
}
