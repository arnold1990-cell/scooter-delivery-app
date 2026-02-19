package com.scooter.app.modules.admin.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AnalyticsSummaryResponse {
    private long totalDeliveries;
    private BigDecimal totalRevenue;
    private long activeRiders;
    private double avgDeliveryTimeMinutes;
    private long cancelledDeliveries;
}
