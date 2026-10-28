package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos;

import java.time.LocalDate;

// A discount as shown next to an item or a cart line - just enough for the
// frontend to display "15% off until <date>".
public record ItemDiscountDto(
        Integer discountId,
        Integer percentage,
        LocalDate expireDate
) { }
