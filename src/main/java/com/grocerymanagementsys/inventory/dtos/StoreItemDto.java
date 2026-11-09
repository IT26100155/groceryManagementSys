package com.grocerymanagementsys.inventory.dtos;

import com.grocerymanagementsys.discount.dtos.ItemDiscountDto;

import java.math.BigDecimal;
import java.util.List;

public record StoreItemDto(
        Long id,
        String name,
        String brand,
        String description,
        BigDecimal price,
        BigDecimal discountedPrice,
        List<ItemDiscountDto> discounts,
        Integer availableStock,
        List<Long> categoryIds,
        List<String> categoryNames,
        String imageUrl,
        String storageType
) { }
