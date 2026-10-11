package com.grocerymanagementsys.cart.services;

import com.grocerymanagementsys.cart.dtos.AddItemDto;
import com.grocerymanagementsys.cart.dtos.CartItemDto;
import com.grocerymanagementsys.cart.dtos.CartResponseDto;
import com.grocerymanagementsys.cart.entities.CartUnit;
import com.grocerymanagementsys.cart.repositories.CartRepository;
import com.grocerymanagementsys.discount.dtos.ItemPricing;
import com.grocerymanagementsys.discount.services.DiscountPricingService;
import com.grocerymanagementsys.inventory.entities.Item;
import com.grocerymanagementsys.inventory.services.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final DiscountPricingService discountPricingService;
    private final ItemService itemService;


    public CartResponseDto addItem(Integer customerId, AddItemDto dto) {
        itemService.findItemOrThrow(dto.itemId());

        //search for existing record or create a new one.
        CartUnit line = cartRepository.findByCustomerIdAndItemId(customerId, dto.itemId())
                .orElseGet(() -> {
                    CartUnit newLine = new CartUnit();
                    newLine.setCustomerId(customerId);
                    newLine.setItemId(dto.itemId());
                    newLine.setQuantity(0);
                    return newLine;
                });

        line.setQuantity(line.getQuantity() + dto.quantity());
        cartRepository.save(line);

        return getCart(customerId);
    }

    public CartResponseDto removeItem(Integer customerId, Integer itemId) {
        cartRepository.findByCustomerIdAndItemId(customerId, itemId)
                .ifPresent(cartRepository::delete);
        return getCart(customerId);
    }

    public CartResponseDto getCart(Integer customerId) {
        List<CartUnit> lines = cartRepository.findByCustomerId(customerId);

        // Load each distinct item once, then price them all together (2 discount queries total).
        Map<Integer, Item> itemsById = lines.stream()
                .map(CartUnit::getItemId)
                .distinct()
                .map(itemService::findItemOrThrow)
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Integer, ItemPricing> pricing = discountPricingService.priceItems(itemsById.values());

        List<CartItemDto> itemDtos = lines.stream()
                .map(line -> {
                    Item item = itemsById.get(line.getItemId());
                    ItemPricing itemPricing = pricing.get(item.getId());
                    BigDecimal unitPrice = itemPricing.discountedPrice();
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(line.getQuantity()));
                    return new CartItemDto(
                            item.getId(), item.getName(), line.getQuantity(),
                            item.getPrice(), unitPrice, itemPricing.discounts(), lineTotal);
                })
                .toList();

        BigDecimal total = itemDtos.stream()
                .map(CartItemDto::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(itemDtos, total);
    }

    // Called by OrderService after an order is successfully placed.
    public void clearCart(Integer customerId) {
        cartRepository.deleteByCustomerId(customerId);
    }
}
