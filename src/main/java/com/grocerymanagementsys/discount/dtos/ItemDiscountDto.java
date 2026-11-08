package com.grocerymanagementsys.discount.dtos;

import java.time.LocalDate;

public record ItemDiscountDto(
        Integer discountId,
        Integer percentage,
        LocalDate expireDate
) { }
