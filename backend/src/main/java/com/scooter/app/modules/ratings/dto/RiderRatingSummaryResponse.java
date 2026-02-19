package com.scooter.app.modules.ratings.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RiderRatingSummaryResponse {
    private double avgRating;
    private long totalRatings;
}
