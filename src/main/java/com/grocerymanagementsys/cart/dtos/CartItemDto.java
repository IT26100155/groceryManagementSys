package com.grocerymanagementsys.cart.dtos;

import com.grocerymanagementsys.discount.dtos.ItemDiscountDto;

import java.math.BigDecimal;
import java.util.List;

public record CartItemDto(
        Integer itemId,
        String itemName,
        Integer quantity,
        BigDecimal originalUnitPrice, // normal price of one unit
        BigDecimal discountedUnitPrice, // price of one unit after the best discount (== original when none)
        List<ItemDiscountDto> discounts, // every applicable discount, best first
        BigDecimal lineTotal // discountedUnitPrice * quantity
) { }
