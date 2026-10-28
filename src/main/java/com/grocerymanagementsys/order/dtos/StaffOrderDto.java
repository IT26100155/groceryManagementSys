package com.grocerymanagementsys.order.dtos;

import com.grocerymanagementsys.order.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record StaffOrderDto(
        Integer id,
        String customerName,
        String customerEmail,
        String address,
        LocalDateTime date,
        BigDecimal total,
        OrderStatus status,
        List<Line> items
) {
    public record Line(String itemName, Integer quantity, BigDecimal unitPrice) { }
}
