package com.scooter.app.modules.deliveries;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeliveryStatusHistoryRepository extends JpaRepository<DeliveryStatusHistory, UUID> {
    List<DeliveryStatusHistory> findByDeliveryIdOrderByCreatedAtAsc(UUID deliveryId);

    List<DeliveryStatusHistory> findByDeliveryIdAndStatusOrderByCreatedAtAsc(UUID deliveryId, DeliveryStatus status);

    DeliveryStatusHistory findTopByDeliveryIdOrderByCreatedAtDesc(UUID deliveryId);
}
