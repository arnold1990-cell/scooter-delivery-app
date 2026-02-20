package com.scooter.dispatch.service;

import com.scooter.dispatch.events.DeliveryCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DispatchEventListener {

    private final DispatchService dispatchService;

    public DispatchEventListener(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @KafkaListener(topics = "delivery.created", groupId = "dispatch-service")
    public void consumeDeliveryCreated(DeliveryCreatedEvent event) {
        dispatchService.onDeliveryCreated(event);
    }
}
