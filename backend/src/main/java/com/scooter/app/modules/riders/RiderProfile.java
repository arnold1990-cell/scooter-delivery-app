package com.scooter.app.modules.riders;

import com.scooter.app.modules.iam.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rider_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RiderProfile {
    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @Column(name = "license_number")
    private String licenseNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_status", columnDefinition = "approval_status", nullable = false)
    private ApprovalStatus approvalStatus;

    @Column(name = "is_online", nullable = false)
    private Boolean isOnline;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
