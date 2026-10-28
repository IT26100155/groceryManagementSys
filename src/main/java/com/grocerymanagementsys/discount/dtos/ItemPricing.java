package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos;

import java.math.BigDecimal;
import java.util.List;

// Result of pricing one item: every discount that applies (best first) and the
// unit price after applying the best one.
public record ItemPricing(
        List<ItemDiscountDto> discounts,
        BigDecimal discountedPrice
) { }
