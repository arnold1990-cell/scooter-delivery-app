package com.scooter.app.modules.customers;

import com.scooter.app.modules.customers.dto.CustomerDeliveryHistoryItem;
import com.scooter.app.modules.deliveries.Delivery;
import com.scooter.app.modules.deliveries.DeliveryRepository;
import com.scooter.app.modules.deliveries.DeliveryStatus;
import com.scooter.app.modules.deliveries.DeliveryStatusHistory;
import com.scooter.app.modules.deliveries.DeliveryStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerDeliveryController {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryStatusHistoryRepository historyRepository;

    @GetMapping("/{customerId}/deliveries")
    public Page<CustomerDeliveryHistoryItem> history(@PathVariable UUID customerId,
                                                     @RequestParam(required = false) DeliveryStatus status,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Delivery> deliveries = status == null
                ? deliveryRepository.findByCustomerId(customerId, pageable)
                : deliveryRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        return deliveries.map(this::toItem);
    }

    private CustomerDeliveryHistoryItem toItem(Delivery delivery) {
        DeliveryStatusHistory latest = historyRepository.findTopByDeliveryIdOrderByCreatedAtDesc(delivery.getId());
        return CustomerDeliveryHistoryItem.builder()
                .id(delivery.getId())
                .pickupAddress(delivery.getPickupAddress())
                .dropoffAddress(delivery.getDropoffAddress())
                .price(delivery.getPrice())
                .latestStatus(latest == null ? delivery.getStatus() : latest.getStatus())
                .latestStatusAt(latest == null ? delivery.getUpdatedAt() : latest.getCreatedAt())
                .createdAt(delivery.getCreatedAt())
                .build();
    }
}
