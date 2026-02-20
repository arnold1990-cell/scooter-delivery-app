package com.scooter.tracking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LocationPingRequest(
        @NotBlank String deliveryId,
        @NotBlank String riderId,
        @NotNull Double latitude,
        @NotNull Double longitude,
        @NotNull Double speedKph
) {}
