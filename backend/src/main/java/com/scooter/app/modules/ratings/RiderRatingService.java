package com.scooter.app.modules.ratings;

import com.scooter.app.modules.deliveries.Delivery;
import com.scooter.app.modules.deliveries.DeliveryRepository;
import com.scooter.app.modules.deliveries.DeliveryStatus;
import com.scooter.app.modules.iam.User;
import com.scooter.app.modules.iam.UserRepository;
import com.scooter.app.modules.ratings.dto.CreateRatingRequest;
import com.scooter.app.modules.ratings.dto.RiderRatingResponse;
import com.scooter.app.modules.ratings.dto.RiderRatingSummaryResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RiderRatingService {
    private final RiderRatingRepository riderRatingRepository;
    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Transactional
    public RiderRatingResponse rateDelivery(String customerEmail, UUID deliveryId, CreateRatingRequest request) {
        User customer = userRepository.findByEmail(customerEmail).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        if (!delivery.getCustomerId().equals(customer.getId())) {
            throw new IllegalArgumentException("Customer can rate only own deliveries");
        }
        if (delivery.getStatus() != DeliveryStatus.DELIVERED) {
            throw new IllegalArgumentException("Delivery must be DELIVERED before rating");
        }
        if (delivery.getRiderId() == null) {
            throw new IllegalArgumentException("Delivery has no assigned rider");
        }
        riderRatingRepository.findByDeliveryId(deliveryId).ifPresent(existing -> {
            throw new IllegalArgumentException("Delivery already rated");
        });

        RiderRating rating = RiderRating.builder()
                .id(UUID.randomUUID())
                .deliveryId(deliveryId)
                .riderId(delivery.getRiderId())
                .customerId(customer.getId())
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();
        return toResponse(riderRatingRepository.save(rating));
    }

    public RiderRatingSummaryResponse summary(UUID riderId) {
        Double avg = riderRatingRepository.averageRating(riderId);
        long total = riderRatingRepository.countByRiderId(riderId);
        return RiderRatingSummaryResponse.builder().avgRating(avg == null ? 0 : avg).totalRatings(total).build();
    }

    public List<RiderRatingResponse> list(UUID riderId) {
        return riderRatingRepository.findByRiderIdOrderByCreatedAtDesc(riderId).stream().map(this::toResponse).toList();
    }

    private RiderRatingResponse toResponse(RiderRating rating) {
        return RiderRatingResponse.builder()
                .id(rating.getId())
                .deliveryId(rating.getDeliveryId())
                .riderId(rating.getRiderId())
                .customerId(rating.getCustomerId())
                .rating(rating.getRating())
                .comment(rating.getComment())
                .createdAt(rating.getCreatedAt())
                .build();
    }
}
