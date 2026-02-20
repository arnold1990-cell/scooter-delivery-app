package com.scooter.dispatch.dto;

public record RiderScoreRequest(double distanceKm, double etaMinutes, double acceptanceRate, double rating, double cancellationRate) {}
