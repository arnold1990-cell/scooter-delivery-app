package com.scooter.app.modules.ratings;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RiderRatingRepository extends JpaRepository<RiderRating, UUID> {
    Optional<RiderRating> findByDeliveryId(UUID deliveryId);

    List<RiderRating> findByRiderIdOrderByCreatedAtDesc(UUID riderId);

    @Query("select avg(r.rating) from RiderRating r where r.riderId = :riderId")
    Double averageRating(UUID riderId);

    long countByRiderId(UUID riderId);
}
