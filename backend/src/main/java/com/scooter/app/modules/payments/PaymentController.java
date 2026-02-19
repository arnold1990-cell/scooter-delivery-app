package com.scooter.app.modules.payments;

import com.scooter.app.modules.payments.dto.PaymentRequest;
import com.scooter.app.modules.payments.dto.PaymentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/deliveries/{deliveryId}/payment")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public PaymentResponse createOrUpdate(@PathVariable UUID deliveryId, @Valid @RequestBody PaymentRequest request) {
        return paymentService.createOrUpdate(deliveryId, request);
    }

    @GetMapping
    public PaymentResponse get(@PathVariable UUID deliveryId) {
        return paymentService.get(deliveryId);
    }

    @PostMapping("/mark-paid")
    public PaymentResponse markPaid(@PathVariable UUID deliveryId) {
        return paymentService.markPaid(deliveryId);
    }
}
