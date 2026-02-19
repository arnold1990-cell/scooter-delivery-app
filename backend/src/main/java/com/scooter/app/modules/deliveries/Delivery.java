package com.scooter.app.modules.deliveries;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "deliveries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Delivery {
    @Id
    private UUID id;

    @Column(name = "customer_id", nullable = false)
    private UUID customerId;

    @Column(name = "rider_id")
    private UUID riderId;

    @Column(name = "pickup_address", nullable = false)
    private String pickupAddress;

    @Column(name = "dropoff_address", nullable = false)
    private String dropoffAddress;

    @Column(name = "pickup_latitude")
    private BigDecimal pickupLatitude;

    @Column(name = "pickup_longitude")
    private BigDecimal pickupLongitude;

    @Column(name = "dropoff_latitude")
    private BigDecimal dropoffLatitude;

    @Column(name = "dropoff_longitude")
    private BigDecimal dropoffLongitude;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(name = "distance_km")
    private BigDecimal distanceKm;

    @Column(name = "estimated_pickup_time")
    private LocalDateTime estimatedPickupTime;

    @Column(name = "estimated_delivery_time")
    private LocalDateTime estimatedDeliveryTime;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "delivery_status", nullable = false)
    private DeliveryStatus status;

    @Column
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
