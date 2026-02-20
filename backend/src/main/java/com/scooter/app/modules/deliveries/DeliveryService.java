package com.scooter.app.modules.deliveries;

import com.scooter.app.modules.deliveries.dto.*;
import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.notifications.NotificationService;
import com.scooter.app.modules.notifications.NotificationType;
import com.scooter.app.modules.pricing.PricingService;
import com.scooter.app.modules.riders.*;
import com.scooter.app.modules.riders.dto.RiderLocationResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryStatusHistoryRepository historyRepository;
    private final UserRepository userRepository;
    private final RiderRepository riderRepository;
    private final RiderLocationRepository riderLocationRepository;
    private final PricingService pricingService;
    private final NotificationService notificationService;
    private final RouteOptimizationService routeOptimizationService;

    @Transactional
    public DeliveryResponse create(String customerEmail, CreateDeliveryRequest request) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        LocalDateTime now = LocalDateTime.now();
        BigDecimal distanceKm = DistanceUtils.haversine(
                request.getPickupLatitude(),
                request.getPickupLongitude(),
                request.getDropoffLatitude(),
                request.getDropoffLongitude()
        );

        Delivery delivery = Delivery.builder()
                .id(UUID.randomUUID())
                .customerId(customer.getId())
                .pickupAddress(request.getPickupAddress())
                .dropoffAddress(request.getDropoffAddress())
                .pickupLatitude(request.getPickupLatitude())
                .pickupLongitude(request.getPickupLongitude())
                .dropoffLatitude(request.getDropoffLatitude())
                .dropoffLongitude(request.getDropoffLongitude())
                .distanceKm(distanceKm)
                .price(request.getPrice() == null ? pricingService.calculatePrice(distanceKm) : request.getPrice())
                .status(resolveCreateStatus(request.getStatus()))
                .notes(request.getNotes())
                .createdAt(now)
                .updatedAt(now)
                .build();
        estimateTimes(delivery, now);
        Delivery saved = deliveryRepository.save(delivery);
        saveHistory(saved, saved.getStatus(), customer.getId(), ChangedByRole.SYSTEM, "Delivery created", null, null);
        return toResponse(saved);
    }

    private DeliveryStatus resolveCreateStatus(String requestedStatus) {
        if (requestedStatus == null || requestedStatus.isBlank()) {
            return DeliveryStatus.PENDING;
        }
        return DeliveryStatus.from(requestedStatus);
    }

    public List<DeliveryResponse> customerDeliveries(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        return deliveryRepository.findByCustomerId(customer.getId()).stream().map(this::toResponse).toList();
    }

    public List<DeliveryResponse> availableJobs() {
        return deliveryRepository.findByStatus(DeliveryStatus.PENDING).stream().map(this::toResponse).toList();
    }

    public List<DeliveryResponse> riderDeliveries(String riderEmail) {
        User rider = userRepository.findByEmail(riderEmail)
                .orElseThrow(() -> new EntityNotFoundException("Rider not found"));
        return deliveryRepository.findByRiderId(rider.getId()).stream().map(this::toResponse).toList();
    }


    @Transactional
    public DeliveryResponse updateStatus(String actorEmail, ChangedByRole changedByRole, UUID id, UpdateStatusRequest request) {
        User actor = userRepository.findByEmail(actorEmail).orElseThrow(() -> new EntityNotFoundException("User not found"));
        return updateStatus(actor.getId(), changedByRole, id, request);
    }

    @Transactional
    public DeliveryResponse updateStatus(UUID userId, ChangedByRole changedByRole, UUID id, UpdateStatusRequest request) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));

        validateTransition(delivery.getStatus(), request.getStatus());
        assignRiderOnClaim(delivery, userId, changedByRole, request.getStatus());
        validateRiderOwnership(delivery, userId, changedByRole);
        delivery.setStatus(request.getStatus());
        delivery.setUpdatedAt(LocalDateTime.now());
        Delivery saved = deliveryRepository.save(delivery);
        saveHistory(saved, request.getStatus(), userId, changedByRole, request.getNotes(), request.getLatitude(), request.getLongitude());
        createStatusNotifications(saved);
        return toResponse(saved);
    }

    private void assignRiderOnClaim(Delivery delivery, UUID userId, ChangedByRole changedByRole, DeliveryStatus toStatus) {
        if (changedByRole == ChangedByRole.RIDER && toStatus == DeliveryStatus.ASSIGNED && delivery.getRiderId() == null) {
            delivery.setRiderId(userId);
        }
    }

    private void validateRiderOwnership(Delivery delivery, UUID userId, ChangedByRole changedByRole) {
        if (changedByRole != ChangedByRole.RIDER || delivery.getRiderId() == null) {
            return;
        }

        if (!delivery.getRiderId().equals(userId)) {
            throw new IllegalArgumentException("Delivery is assigned to a different rider");
        }
    }

    public List<DeliveryStatusHistoryResponse> history(UUID id) {
        deliveryRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        return historyRepository.findByDeliveryIdOrderByCreatedAtAsc(id).stream().map(this::toHistoryResponse).toList();
    }

    @Transactional
    public DeliveryResponse assignNearest(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        if (delivery.getStatus() != DeliveryStatus.PENDING) {
            throw new IllegalArgumentException("Only PENDING delivery can be assigned");
        }

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);
        List<RiderProfile> availableRiders = riderRepository.findByStatus(RiderStatus.AVAILABLE);
        UUID nearestRiderId = null;
        BigDecimal nearestDistance = null;

        for (RiderProfile rider : availableRiders) {
            Optional<RiderLocation> locationOpt = riderLocationRepository.findById(rider.getUserId());
            if (locationOpt.isEmpty()) {
                continue;
            }
            RiderLocation location = locationOpt.get();
            if (location.getUpdatedAt().isBefore(threshold)) {
                continue;
            }
            BigDecimal distance = DistanceUtils.haversine(
                    delivery.getPickupLatitude(),
                    delivery.getPickupLongitude(),
                    location.getLatitude(),
                    location.getLongitude()
            );
            if (nearestDistance == null || distance.compareTo(nearestDistance) < 0) {
                nearestDistance = distance;
                nearestRiderId = rider.getUserId();
            }
        }

        if (nearestRiderId == null) {
            throw new IllegalArgumentException("No available riders with recent location");
        }

        delivery.setRiderId(nearestRiderId);
        delivery.setStatus(DeliveryStatus.ASSIGNED);
        delivery.setUpdatedAt(LocalDateTime.now());
        estimateTimes(delivery, LocalDateTime.now());
        Delivery saved = deliveryRepository.save(delivery);
        saveHistory(saved, DeliveryStatus.ASSIGNED, nearestRiderId, ChangedByRole.SYSTEM, "Auto-assigned nearest rider", null, null);

        RiderProfile riderProfile = riderRepository.findByUserId(nearestRiderId).orElseThrow(() -> new EntityNotFoundException("Rider not found"));
        riderProfile.setStatus(RiderStatus.BUSY);
        riderRepository.save(riderProfile);

        createAssignedNotification(saved);
        routeOptimizationService.optimizeForDelivery(saved.getId());
        return toResponse(saved);
    }

    public RiderLocationResponse riderLocation(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        if (delivery.getRiderId() == null) {
            throw new IllegalArgumentException("Delivery has no assigned rider");
        }
        RiderLocation location = riderLocationRepository.findById(delivery.getRiderId())
                .orElseThrow(() -> new EntityNotFoundException("Rider location not found"));
        return RiderLocationResponse.builder()
                .riderId(location.getRiderId())
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .updatedAt(location.getUpdatedAt())
                .build();
    }

    public DeliveryEtaResponse eta(UUID deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        if (delivery.getDistanceKm() == null || delivery.getEstimatedDeliveryTime() == null) {
            estimateTimes(delivery, LocalDateTime.now());
            deliveryRepository.save(delivery);
        }
        return DeliveryEtaResponse.builder()
                .distanceKm(delivery.getDistanceKm())
                .estimatedPickupTime(delivery.getEstimatedPickupTime())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .build();
    }

    public Page<DeliveryResponse> adminAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return deliveryRepository.findAll(pageable).map(this::toResponse);
    }

    private void validateTransition(DeliveryStatus from, DeliveryStatus to) {
        Map<DeliveryStatus, Set<DeliveryStatus>> transitions = Map.of(
                DeliveryStatus.PENDING, Set.of(DeliveryStatus.ASSIGNED, DeliveryStatus.CANCELLED),
                DeliveryStatus.ASSIGNED, Set.of(DeliveryStatus.PICKED_UP, DeliveryStatus.CANCELLED),
                DeliveryStatus.PICKED_UP, Set.of(DeliveryStatus.IN_TRANSIT, DeliveryStatus.FAILED),
                DeliveryStatus.IN_TRANSIT, Set.of(DeliveryStatus.DELIVERED, DeliveryStatus.FAILED),
                DeliveryStatus.DELIVERED, Set.of(),
                DeliveryStatus.CANCELLED, Set.of(),
                DeliveryStatus.FAILED, Set.of()
        );

        Set<DeliveryStatus> allowed = transitions.getOrDefault(from, Set.of());
        if (!allowed.contains(to)) {
            throw new IllegalArgumentException("Invalid transition from " + from + " to " + to);
        }
    }

    private void saveHistory(Delivery delivery, DeliveryStatus status, UUID changedByUserId, ChangedByRole changedByRole,
                             String notes, BigDecimal latitude, BigDecimal longitude) {
        historyRepository.save(DeliveryStatusHistory.builder()
                .id(UUID.randomUUID())
                .deliveryId(delivery.getId())
                .status(status)
                .changedByUserId(changedByUserId)
                .changedByRole(changedByRole)
                .notes(notes)
                .latitude(latitude)
                .longitude(longitude)
                .createdAt(LocalDateTime.now())
                .build());
    }

    private void estimateTimes(Delivery delivery, LocalDateTime now) {
        if (delivery.getDistanceKm() == null) {
            delivery.setDistanceKm(DistanceUtils.haversine(
                    delivery.getPickupLatitude(), delivery.getPickupLongitude(),
                    delivery.getDropoffLatitude(), delivery.getDropoffLongitude()));
        }
        BigDecimal speed = new BigDecimal("40.0");
        BigDecimal pickupDistance = delivery.getRiderId() == null ? BigDecimal.ZERO : new BigDecimal("2.0");
        long pickupSeconds = pickupDistance.divide(speed, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("3600")).longValue();
        long deliverySeconds = delivery.getDistanceKm().divide(speed, 6, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("3600")).longValue();
        LocalDateTime pickupTime = now.plusSeconds(pickupSeconds);
        delivery.setEstimatedPickupTime(pickupTime);
        delivery.setEstimatedDeliveryTime(pickupTime.plusSeconds(deliverySeconds));
    }

    private void createAssignedNotification(Delivery delivery) {
        notificationService.create(delivery.getRiderId(), "New delivery assigned: " + delivery.getId(), NotificationType.DELIVERY_ASSIGNED);
        notificationService.create(delivery.getCustomerId(), "Your delivery has been assigned", NotificationType.DELIVERY_ASSIGNED);
    }

    private void createStatusNotifications(Delivery delivery) {
        if (delivery.getStatus() == DeliveryStatus.PICKED_UP) {
            notificationService.create(delivery.getCustomerId(), "Your order has been picked up", NotificationType.DELIVERY_PICKED_UP);
        }
        if (delivery.getStatus() == DeliveryStatus.DELIVERED) {
            notificationService.create(delivery.getCustomerId(), "Your order has been delivered", NotificationType.DELIVERY_DELIVERED);
        }
    }

    private DeliveryStatusHistoryResponse toHistoryResponse(DeliveryStatusHistory history) {
        return DeliveryStatusHistoryResponse.builder()
                .id(history.getId())
                .status(history.getStatus())
                .changedByUserId(history.getChangedByUserId())
                .changedByRole(history.getChangedByRole())
                .notes(history.getNotes())
                .latitude(history.getLatitude())
                .longitude(history.getLongitude())
                .createdAt(history.getCreatedAt())
                .build();
    }

    private DeliveryResponse toResponse(Delivery delivery) {
        return DeliveryResponse.builder()
                .id(delivery.getId())
                .customerId(delivery.getCustomerId())
                .riderId(delivery.getRiderId())
                .pickupAddress(delivery.getPickupAddress())
                .dropoffAddress(delivery.getDropoffAddress())
                .pickupLatitude(delivery.getPickupLatitude())
                .pickupLongitude(delivery.getPickupLongitude())
                .dropoffLatitude(delivery.getDropoffLatitude())
                .dropoffLongitude(delivery.getDropoffLongitude())
                .price(delivery.getPrice())
                .distanceKm(delivery.getDistanceKm())
                .estimatedPickupTime(delivery.getEstimatedPickupTime())
                .estimatedDeliveryTime(delivery.getEstimatedDeliveryTime())
                .status(delivery.getStatus())
                .notes(delivery.getNotes())
                .createdAt(delivery.getCreatedAt())
                .updatedAt(delivery.getUpdatedAt())
                .build();
    }
}
