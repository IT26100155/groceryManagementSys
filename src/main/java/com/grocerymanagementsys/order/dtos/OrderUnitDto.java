package com.grocerymanagementsys.order.dtos;

import java.math.BigDecimal;

public record OrderUnitDto(
        Integer itemId,
        Integer quantity,
        BigDecimal unitPrice
) { }
