package com.scooter.app.modules.payments;

import com.scooter.app.modules.deliveries.DeliveryRepository;
import com.scooter.app.modules.payments.dto.PaymentRequest;
import com.scooter.app.modules.payments.dto.PaymentResponse;
import com.scooter.app.shared.exception.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final DeliveryRepository deliveryRepository;

    @Transactional
    public PaymentResponse createOrUpdate(UUID deliveryId, PaymentRequest request) {
        deliveryRepository.findById(deliveryId).orElseThrow(() -> new EntityNotFoundException("Delivery not found"));
        Payment payment = paymentRepository.findByDeliveryId(deliveryId)
                .orElse(Payment.builder().id(UUID.randomUUID()).deliveryId(deliveryId).createdAt(LocalDateTime.now()).build());
        payment.setAmount(request.getAmount());
        payment.setMethod(request.getMethod());
        payment.setStatus(request.getStatus() == null ? PaymentStatus.PENDING : request.getStatus());
        payment.setUpdatedAt(LocalDateTime.now());
        return toResponse(paymentRepository.save(payment));
    }

    public PaymentResponse get(UUID deliveryId) {
        Payment payment = paymentRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return toResponse(payment);
    }

    @Transactional
    public PaymentResponse markPaid(UUID deliveryId) {
        Payment payment = paymentRepository.findByDeliveryId(deliveryId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        payment.setStatus(PaymentStatus.PAID);
        payment.setUpdatedAt(LocalDateTime.now());
        return toResponse(paymentRepository.save(payment));
    }

    private PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .deliveryId(payment.getDeliveryId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}
