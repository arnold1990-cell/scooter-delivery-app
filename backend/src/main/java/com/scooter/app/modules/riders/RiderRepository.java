package com.scooter.app.modules.riders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiderRepository extends JpaRepository<RiderProfile, UUID> {
    Optional<RiderProfile> findByUserId(UUID userId);

    List<RiderProfile> findByStatus(RiderStatus status);
}
