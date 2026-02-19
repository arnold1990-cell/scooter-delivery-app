package com.scooter.app.modules.payments.dto;

import com.scooter.app.modules.payments.PaymentMethod;
import com.scooter.app.modules.payments.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
    @NotNull
    private PaymentMethod method;
    private PaymentStatus status;
}
