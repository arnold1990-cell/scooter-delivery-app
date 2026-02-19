package com.scooter.app.modules.deliveries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    List<Delivery> findByCustomerId(UUID customerId);

    List<Delivery> findByStatus(DeliveryStatus status);

    List<Delivery> findByRiderId(UUID riderId);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    long countByStatusAndCreatedAtBetween(DeliveryStatus status, LocalDateTime start, LocalDateTime end);

    Page<Delivery> findByCustomerIdAndStatus(UUID customerId, DeliveryStatus status, Pageable pageable);

    Page<Delivery> findByCustomerId(UUID customerId, Pageable pageable);
}
