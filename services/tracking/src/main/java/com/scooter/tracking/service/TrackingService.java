package com.scooter.tracking.service;

import com.scooter.tracking.dto.LocationPingRequest;
import com.scooter.tracking.events.RiderLocationUpdatedEvent;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class TrackingService {

    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final Map<String, List<SseEmitter>> channels = new ConcurrentHashMap<>();

    public TrackingService(StringRedisTemplate redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public RiderLocationUpdatedEvent upsertLocation(LocationPingRequest ping) {
        redisTemplate.opsForValue().set(
                "rider:" + ping.riderId() + ":loc",
                ping.latitude() + "," + ping.longitude() + "," + ping.speedKph(),
                Duration.ofSeconds(30)
        );
        int etaSeconds = (int) Math.max(60, 2400 - (ping.speedKph() * 20));
        RiderLocationUpdatedEvent event = new RiderLocationUpdatedEvent(
                ping.deliveryId(), ping.riderId(), ping.latitude(), ping.longitude(), ping.speedKph(), etaSeconds, OffsetDateTime.now());
        kafkaTemplate.send("rider.location.updated", ping.deliveryId(), event);
        return event;
    }

    public SseEmitter subscribe(String deliveryId) {
        SseEmitter emitter = new SseEmitter(0L);
        channels.computeIfAbsent(deliveryId, k -> new java.util.concurrent.CopyOnWriteArrayList<>()).add(emitter);
        emitter.onCompletion(() -> channels.getOrDefault(deliveryId, List.of()).remove(emitter));
        emitter.onTimeout(() -> channels.getOrDefault(deliveryId, List.of()).remove(emitter));
        return emitter;
    }

    public void broadcast(RiderLocationUpdatedEvent event) {
        channels.getOrDefault(event.deliveryId(), List.of()).forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("location").data(event));
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
    }
}
