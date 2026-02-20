package com.scooter.delivery.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDeliveryRequest(
        @NotBlank String pickupAddress,
        @NotBlank String dropoffAddress
) {}
