package com.scooter.app.modules.deliveries.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateDeliveryRequest {
    @NotBlank
    private String pickupAddress;

    @NotBlank
    private String dropoffAddress;

    @DecimalMin(value = "0.0")
    private BigDecimal price;

    private String notes;

    private String status;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal pickupLatitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal pickupLongitude;

    @DecimalMin("-90.0")
    @DecimalMax("90.0")
    private BigDecimal dropoffLatitude;

    @DecimalMin("-180.0")
    @DecimalMax("180.0")
    private BigDecimal dropoffLongitude;

    @Valid
    private Location pickupLocation;

    @Valid
    private Location dropoffLocation;

    public BigDecimal getPickupLatitude() {
        return pickupLatitude != null ? pickupLatitude : pickupLocation != null ? pickupLocation.getLatitude() : null;
    }

    public BigDecimal getPickupLongitude() {
        return pickupLongitude != null ? pickupLongitude : pickupLocation != null ? pickupLocation.getLongitude() : null;
    }

    public BigDecimal getDropoffLatitude() {
        return dropoffLatitude != null ? dropoffLatitude : dropoffLocation != null ? dropoffLocation.getLatitude() : null;
    }

    public BigDecimal getDropoffLongitude() {
        return dropoffLongitude != null ? dropoffLongitude : dropoffLocation != null ? dropoffLocation.getLongitude() : null;
    }

    @AssertTrue(message = "pickupLatitude and pickupLongitude are required (either flat fields or pickupLocation).")
    @JsonIgnore
    public boolean hasPickupCoordinates() {
        return getPickupLatitude() != null && getPickupLongitude() != null;
    }

    @AssertTrue(message = "dropoffLatitude and dropoffLongitude are required (either flat fields or dropoffLocation).")
    @JsonIgnore
    public boolean hasDropoffCoordinates() {
        return getDropoffLatitude() != null && getDropoffLongitude() != null;
    }

    @Data
    public static class Location {
        @NotNull
        @DecimalMin("-90.0")
        @DecimalMax("90.0")
        private BigDecimal latitude;

        @NotNull
        @DecimalMin("-180.0")
        @DecimalMax("180.0")
        private BigDecimal longitude;
    }
}
