package com.scooter.app.modules.deliveries;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "delivery_status_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeliveryStatusHistory {
    @Id
    private UUID id;

    @Column(name = "delivery_id", nullable = false)
    private UUID deliveryId;

    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "delivery_status", nullable = false)
    private DeliveryStatus status;

    @Column(name = "changed_by_user_id")
    private UUID changedByUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "changed_by_role")
    private ChangedByRole changedByRole;

    @Column
    private String notes;

    @Column
    private BigDecimal latitude;

    @Column
    private BigDecimal longitude;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
