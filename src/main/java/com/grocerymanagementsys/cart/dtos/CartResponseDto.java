package com.grocerymanagementsys.cart.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(
        List<CartItemDto> items,
        BigDecimal total
) { }
