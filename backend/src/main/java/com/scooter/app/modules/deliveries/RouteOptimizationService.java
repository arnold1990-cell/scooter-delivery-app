package com.scooter.app.modules.deliveries;

import java.util.UUID;

public interface RouteOptimizationService {
    // TODO: implement batching and multi-stop route optimization in future iteration.
    void optimizeForDelivery(UUID deliveryId);
}
