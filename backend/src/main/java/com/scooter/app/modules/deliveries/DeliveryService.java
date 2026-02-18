package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.deliveries.dto.CreateDeliveryRequest;
import com.scooter.app.modules.deliveries.dto.DeliveryResponse;
import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Transactional
    public DeliveryResponse create(String customerEmail, CreateDeliveryRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Delivery delivery = Delivery.builder()
                .id(UUID.randomUUID())
                .customerId(customer.getId())
                .pickupAddress(request.getPickupAddress())
                .dropoffAddress(request.getDropoffAddress())
                .price(request.getPrice())
                .status(DeliveryStatus.REQUESTED)
                .notes(request.getNotes())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        return toResponse(deliveryRepository.save(delivery));
    }

    public List<DeliveryResponse> customerDeliveries(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return deliveryRepository.findByCustomerId(customer.getId()).stream().map(this::toResponse).toList();
    }

    public List<DeliveryResponse> availableJobs() {
        return deliveryRepository.findByStatus(DeliveryStatus.REQUESTED).stream().map(this::toResponse).toList();
    }

    public List<DeliveryResponse> riderDeliveries(String riderEmail) {
        User rider = userRepository.findByEmail(riderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Rider not found"));
        return deliveryRepository.findByRiderId(rider.getId()).stream().map(this::toResponse).toList();
    }

    @Transactional
    public DeliveryResponse updateStatus(String riderEmail, UUID id, DeliveryStatus requestedStatus) {
        User rider = userRepository.findByEmail(riderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Rider not found"));
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        if (requestedStatus == DeliveryStatus.ACCEPTED) {
            if (delivery.getStatus() != DeliveryStatus.REQUESTED) {
                throw new IllegalArgumentException("Only REQUESTED delivery can be accepted");
            }
            delivery.setRiderId(rider.getId());
            delivery.setStatus(DeliveryStatus.ASSIGNED);
        } else {
            if (delivery.getRiderId() == null || !delivery.getRiderId().equals(rider.getId())) {
                throw new IllegalArgumentException("Rider can update only their assigned deliveries");
            }
            Set<DeliveryStatus> allowed = Set.of(DeliveryStatus.PICKED_UP, DeliveryStatus.IN_TRANSIT, DeliveryStatus.DELIVERED, DeliveryStatus.REJECTED);
            if (!allowed.contains(requestedStatus)) {
                throw new IllegalArgumentException("Invalid status transition");
            }
            delivery.setStatus(requestedStatus);
        }

        delivery.setUpdatedAt(LocalDateTime.now());
        return toResponse(deliveryRepository.save(delivery));
    }

    public Page<DeliveryResponse> adminAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryRepository.findAll(pageable).map(this::toResponse);
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .customerId(delivery.getCustomerId())
                .riderId(delivery.getRiderId())
                .pickupAddress(delivery.getPickupAddress())
                .dropoffAddress(delivery.getDropoffAddress())
                .price(delivery.getPrice())
                .status(delivery.getStatus())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
