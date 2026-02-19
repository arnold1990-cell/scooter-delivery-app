package com.scooter.app.modules.ratings;

import com.scooter.app.modules.ratings.dto.CreateRatingRequest;
import com.scooter.app.modules.ratings.dto.RiderRatingResponse;
import com.scooter.app.modules.ratings.dto.RiderRatingSummaryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RiderRatingController {
    private final RiderRatingService riderRatingService;

    @PostMapping("/deliveries/{id}/rate")
    public RiderRatingResponse rate(Authentication authentication, @PathVariable UUID id, @Valid @RequestBody CreateRatingRequest request) {
        return riderRatingService.rateDelivery(authentication.getName(), id, request);
    }

    @GetMapping("/riders/{id}/ratings/summary")
    public RiderRatingSummaryResponse summary(@PathVariable UUID id) {
        return riderRatingService.summary(id);
    }

    @GetMapping("/riders/{id}/ratings")
    public List<RiderRatingResponse> list(@PathVariable UUID id) {
        return riderRatingService.list(id);
    }
}
