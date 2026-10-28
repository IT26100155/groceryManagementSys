package com.grocerymanagementsys.order.dtos;

import com.grocerymanagementsys.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDto(
        Integer id,
        Integer customerId,
        OrderStatus status,
        BigDecimal total,
        LocalDateTime date,
        List<OrderUnitDto> items
) { }
