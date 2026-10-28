package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateDiscountItemsDto(
        @NotEmpty List<@NotNull Integer> itemIds
) { }
