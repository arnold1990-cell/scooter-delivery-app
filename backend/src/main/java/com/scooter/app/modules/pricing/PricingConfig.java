package com.scooter.app.modules.pricing;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PricingConfig {
    @Id
    private Short id;

    @Column(name = "base_fare", nullable = false)
    private BigDecimal baseFare;

    @Column(name = "per_km_rate", nullable = false)
    private BigDecimal perKmRate;

    @Column(name = "peak_multiplier", nullable = false)
    private BigDecimal peakMultiplier;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
