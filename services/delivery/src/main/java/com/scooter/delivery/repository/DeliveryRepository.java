package com.scooter.delivery.repository;

import com.scooter.delivery.entity.DeliveryEntity;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<DeliveryEntity, UUID> {
    Optional<DeliveryEntity> findByIdempotencyKey(String idempotencyKey);
}
