package com.grocerymanagementsys.cart.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddItemDto(
        @NotNull Integer itemId,
        @NotNull @Min(1) Integer quantity
) { }
