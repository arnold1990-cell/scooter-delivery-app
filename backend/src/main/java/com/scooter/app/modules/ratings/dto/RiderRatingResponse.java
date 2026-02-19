package com.scooter.app.modules.ratings.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RiderRatingResponse {
    private UUID id;
    private UUID deliveryId;
    private UUID riderId;
    private UUID customerId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
}
