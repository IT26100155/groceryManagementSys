package com.grocerymanagementsys.discount.dtos;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DiscountRequestDto(
        @NotNull @Min(1) @Max(100) Integer percentage,
        BigDecimal minPrice,
        @NotNull LocalDate startDate,
        @NotNull LocalDate expireDate,
        @NotNull Integer employeeId,
        @NotNull Boolean storeWide,
        List<@NotNull Integer> itemIds    // required (non-empty) when storeWide is false; ignored when true
) { }
