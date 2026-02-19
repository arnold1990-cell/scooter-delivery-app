package com.scooter.app.modules.pricing;

import com.scooter.app.modules.pricing.dto.PricingConfigRequest;
import com.scooter.app.modules.pricing.dto.PricingConfigResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class PricingController {

    private final PricingService pricingService;

    @GetMapping("/pricing-config")
    public PricingConfigResponse getConfig() {
        return pricingService.getConfig();
    }

    @PutMapping("/pricing-config")
    @PreAuthorize("hasRole('ADMIN')")
    public PricingConfigResponse update(@Valid @RequestBody PricingConfigRequest request) {
        return pricingService.update(request);
    }
}
