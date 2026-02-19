package com.scooter.app.modules.riders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rider_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderLocation {
    @Id
    @Column(name = "rider_id")
    private UUID riderId;

    @Column(nullable = false)
    private BigDecimal latitude;

    @Column(nullable = false)
    private BigDecimal longitude;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
