package com.scooter.app.modules.pricing;

import com.scooter.app.modules.pricing.dto.PricingConfigRequest;
import com.scooter.app.modules.pricing.dto.PricingConfigResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingService {
    private static final short CONFIG_ID = 1;

    private final PricingConfigRepository pricingConfigRepository;

    public PricingConfigResponse getConfig() {
        return toResponse(getConfigEntity());
    }

    @Transactional
    public PricingConfigResponse update(PricingConfigRequest request) {
        PricingConfig config = getConfigEntity();
        config.setBaseFare(request.getBaseFare());
        config.setPerKmRate(request.getPerKmRate());
        config.setPeakMultiplier(request.getPeakMultiplier());
        config.setUpdatedAt(LocalDateTime.now());
        return toResponse(pricingConfigRepository.save(config));
    }

    public BigDecimal calculatePrice(BigDecimal distanceKm) {
        PricingConfig config = getConfigEntity();
        BigDecimal subtotal = config.getBaseFare().add(distanceKm.multiply(config.getPerKmRate()));
        return subtotal.multiply(config.getPeakMultiplier()).setScale(2, RoundingMode.HALF_UP);
    }

    private PricingConfig getConfigEntity() {
        return pricingConfigRepository.findById(CONFIG_ID)
                .orElseThrow(() -> new EntityNotFoundException("Pricing config not found"));
    }

    private PricingConfigResponse toResponse(PricingConfig config) {
        return PricingConfigResponse.builder()
                .baseFare(config.getBaseFare())
                .perKmRate(config.getPerKmRate())
                .peakMultiplier(config.getPeakMultiplier())
                .updatedAt(config.getUpdatedAt())
                .build();
    }
}
