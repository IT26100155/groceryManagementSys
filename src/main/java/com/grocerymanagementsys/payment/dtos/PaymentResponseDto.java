package com.grocerymanagementsys.payment.dtos;

import com.grocerymanagementsys.payment.enums.PaymentState;
import com.grocerymanagementsys.payment.enums.PaymentType;

import java.time.LocalDateTime;

public record PaymentResponseDto (
        Integer id,
        Integer orderId,
        PaymentType type,
        LocalDateTime date,
        LocalDateTime expiryTime,
        PaymentState state
){
}
