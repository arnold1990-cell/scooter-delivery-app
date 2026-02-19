package com.scooter.app.modules.deliveries;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class NoOpRouteOptimizationService implements RouteOptimizationService {
    @Override
    public void optimizeForDelivery(UUID deliveryId) {
        // TODO: future route optimization implementation.
    }
}
