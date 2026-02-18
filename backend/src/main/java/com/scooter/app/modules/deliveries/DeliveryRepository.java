package com.scooter.app.modules.deliveries;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID> {
    List<Delivery> findByCustomerId(UUID customerId);
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByRiderId(UUID riderId);
    Page<Delivery> findAll(Pageable pageable);
}
