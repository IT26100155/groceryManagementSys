package com.grocerymanagementsys.payment.dtos;

import com.grocerymanagementsys.payment.enums.PaymentType;
import jakarta.validation.constraints.NotNull;

public record InitiatePaymentDto(
        @NotNull Integer orderId,
        @NotNull PaymentType type

){
}
