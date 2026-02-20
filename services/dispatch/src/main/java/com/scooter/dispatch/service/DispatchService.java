package com.scooter.dispatch.service;

import com.scooter.dispatch.dto.RiderScoreRequest;
import com.scooter.dispatch.events.DeliveryAssignedEvent;
import com.scooter.dispatch.events.DeliveryCreatedEvent;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public DispatchService(StringRedisTemplate redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public double score(RiderScoreRequest input) {
        return (100 - (input.distanceKm() * 10)) + (100 - (input.etaMinutes() * 2))
                + (input.acceptanceRate() * 40) + (input.rating() * 20) - (input.cancellationRate() * 30);
    }

    public void onDeliveryCreated(DeliveryCreatedEvent event) {
        String lockKey = "lock:delivery:" + event.deliveryId();
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(lockKey, "assigning", Duration.ofSeconds(30));
        if (!Boolean.TRUE.equals(locked)) {
            return;
        }

        List<String> candidateRiders = List.of("rider-101", "rider-202", "rider-303");
        String winner = candidateRiders.getFirst(); // first-accept-wins placeholder
        kafkaTemplate.send("delivery.assigned", event.deliveryId().toString(),
                new DeliveryAssignedEvent(event.deliveryId(), winner, 89.5, OffsetDateTime.now()));
        redisTemplate.opsForValue().set("delivery:" + event.deliveryId() + ":rider", winner, Duration.ofHours(2));
    }

    public void acceptOffer(UUID deliveryId, String riderId) {
        String lockKey = "lease:accept:" + deliveryId;
        Boolean lease = redisTemplate.opsForValue().setIfAbsent(lockKey, riderId, Duration.ofSeconds(10));
        if (Boolean.TRUE.equals(lease)) {
            kafkaTemplate.send("delivery.assigned", deliveryId.toString(),
                    new DeliveryAssignedEvent(deliveryId, riderId, 99.0, OffsetDateTime.now()));
        }
    }
}
