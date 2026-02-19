package com.scooter.app.modules.admin;

import com.scooter.app.modules.admin.dto.AnalyticsSummaryResponse;
import com.scooter.app.modules.deliveries.*;
import com.scooter.app.modules.payments.PaymentRepository;
import com.scooter.app.modules.payments.PaymentStatus;
import com.scooter.app.modules.riders.RiderLocationRepository;
import com.scooter.app.modules.riders.RiderRepository;
import com.scooter.app.modules.riders.RiderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/analytics")
public class AdminAnalyticsController {
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final RiderRepository riderRepository;
    private final RiderLocationRepository riderLocationRepository;
    private final DeliveryStatusHistoryRepository historyRepository;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public AnalyticsSummaryResponse summary(@RequestParam LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        long totalDeliveries = deliveryRepository.countByCreatedAtBetween(start, end);
        long cancelled = deliveryRepository.countByStatusAndCreatedAtBetween(DeliveryStatus.CANCELLED, start, end);

        BigDecimal totalRevenue = paymentRepository.findAll().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .filter(p -> !p.getCreatedAt().isBefore(start) && p.getCreatedAt().isBefore(end))
                .map(p -> p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime threshold = LocalDateTime.now().minusMinutes(2);
        long activeRiders = riderLocationRepository.findByUpdatedAtAfter(threshold).stream()
                .filter(l -> riderRepository.findByUserId(l.getRiderId()).map(r -> r.getStatus() != RiderStatus.OFFLINE).orElse(false))
                .count();

        List<Delivery> delivered = deliveryRepository.findByStatus(DeliveryStatus.DELIVERED);
        double avgMinutes = 0;
        if (!delivered.isEmpty()) {
            long totalMinutes = 0;
            int count = 0;
            for (Delivery delivery : delivered) {
                List<DeliveryStatusHistory> histories = historyRepository.findByDeliveryIdAndStatusOrderByCreatedAtAsc(delivery.getId(), DeliveryStatus.DELIVERED);
                if (!histories.isEmpty()) {
                    totalMinutes += Duration.between(delivery.getCreatedAt(), histories.get(0).getCreatedAt()).toMinutes();
                    count++;
                }
            }
            avgMinutes = count == 0 ? 0 : (double) totalMinutes / count;
        }

        return AnalyticsSummaryResponse.builder()
                .totalDeliveries(totalDeliveries)
                .totalRevenue(totalRevenue)
                .activeRiders(activeRiders)
                .avgDeliveryTimeMinutes(avgMinutes)
                .cancelledDeliveries(cancelled)
                .build();
    }
}
