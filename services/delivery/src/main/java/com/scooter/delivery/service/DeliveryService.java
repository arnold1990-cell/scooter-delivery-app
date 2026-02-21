package com.scooter.delivery.service;

import com.scooter.delivery.dto.CreateDeliveryRequest;
import com.scooter.delivery.entity.DeliveryEntity;
import com.scooter.delivery.entity.DeliveryStatus;
import com.scooter.delivery.events.DeliveryEvent;
import com.scooter.delivery.repository.DeliveryRepository;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeliveryService {

    private static final Map<DeliveryStatus, Set<DeliveryStatus>> TRANSITIONS = Map.of(
            DeliveryStatus.PENDING, Set.of(DeliveryStatus.ASSIGNED, DeliveryStatus.CANCELLED),
            DeliveryStatus.ASSIGNED, Set.of(DeliveryStatus.PICKED_UP, DeliveryStatus.CANCELLED, DeliveryStatus.FAILED),
            DeliveryStatus.PICKED_UP, Set.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.FAILED),
            DeliveryStatus.IN_TRANSIT, Set.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED)
    );

    private final DeliveryRepository repository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DeliveryService(DeliveryRepository repository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.repository = repository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    public DeliveryEntity createDelivery(String customerId, CreateDeliveryRequest request, String idempotencyKey) {
        if (idempotencyKey != null) {
            var existing = repository.findByIdempotencyKey(idempotencyKey);
            if (existing.isPresent()) return existing.get();
        }
        DeliveryEntity entity = new DeliveryEntity();
        entity.setCustomerId(customerId);
        entity.setPickupAddress(request.pickupAddress());
        entity.setDropoffAddress(request.dropoffAddress());
        entity.setIdempotencyKey(idempotencyKey);
        entity.setStatus(DeliveryStatus.PENDING);
        var saved = repository.save(entity);
        kafkaTemplate.send("delivery.created", saved.getId().toString(),
                new DeliveryEvent(saved.getId(), customerId, saved.getPickupAddress(), saved.getDropoffAddress(), saved.getStatus().name(), OffsetDateTime.now()));
        return saved;
    }

    @Transactional
    public DeliveryEntity transition(UUID id, DeliveryStatus nextStatus) {
        var delivery = repository.findById(id).orElseThrow();
        var allowed = TRANSITIONS.getOrDefault(delivery.getStatus(), Set.of());
        if (!allowed.contains(nextStatus)) {
            throw new IllegalStateException("Invalid transition %s -> %s".formatted(delivery.getStatus(), nextStatus));
        }
        delivery.setStatus(nextStatus);
        var saved = repository.save(delivery);
        kafkaTemplate.send("delivery." + nextStatus.name().toLowerCase(), id.toString(),
                new DeliveryEvent(saved.getId(), saved.getCustomerId(), saved.getPickupAddress(), saved.getDropoffAddress(), saved.getStatus().name(), OffsetDateTime.now()));
        return saved;
    }
}
