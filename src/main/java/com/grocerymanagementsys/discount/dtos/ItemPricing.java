package com.grocerymanagementsys.discount.dtos;

import java.math.BigDecimal;
import java.util.List;

public record ItemPricing(
        List<ItemDiscountDto> discounts,
        BigDecimal discountedPrice
) { }
