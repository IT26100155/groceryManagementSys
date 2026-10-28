package com.grocerymanagementsys.order.dtos;

import java.math.BigDecimal;

public record OrderUnitDto(
        Long itemId,
        Integer quantity,
        BigDecimal unitPrice
) { }
