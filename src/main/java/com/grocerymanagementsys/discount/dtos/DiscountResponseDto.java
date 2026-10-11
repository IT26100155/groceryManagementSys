package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DiscountResponseDto(
        Integer id,
        Integer percentage,
        BigDecimal minPrice,
        LocalDate startDate,
        LocalDate expireDate,
        boolean active,
        Integer employeeId,
        boolean storeWide,
        List<Integer> itemIds     // empty for store-wide discounts
) { }
