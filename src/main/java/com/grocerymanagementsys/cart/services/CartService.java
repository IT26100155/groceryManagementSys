package com.grocerymanagementsys.cart.services;

import com.grocerymanagementsys.cart.dtos.AddItemDto;
import com.grocerymanagementsys.cart.dtos.CartItemDto;
import com.grocerymanagementsys.cart.dtos.CartResponseDto;
import com.grocerymanagementsys.cart.entities.CartUnit;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.customer.services.CustomerService;
import com.grocerymanagementsys.discount.dtos.ItemPricing;
import com.grocerymanagementsys.discount.services.DiscountPricingService;
import com.grocerymanagementsys.inventory.entities.Item;
import com.grocerymanagementsys.inventory.services.InventoryService;
import com.grocerymanagementsys.order.exceptions.StockNotSufficient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CartService {
    private final DiscountPricingService discountPricingService;
    private final CustomerService customerService;
    private final InventoryService inventoryService;

    @Transactional
    public CartResponseDto addItem(Integer customerId, AddItemDto dto) {
        Customer customer = customerService.findCustomerOrThrow(customerId);

        CartUnit line = customer.getCartUnits().stream()
                .filter(u -> u.getItem().getId().equals(dto.itemId()))
                .findFirst()
                .orElse(null);

        Item item = line != null ? line.getItem() : inventoryService.findItemOrThrow(dto.itemId());
        int wanted = (line != null ? line.getQuantity() : 0) + dto.quantity();
        if (item.getAvailableStock() < wanted) {
            throw new StockNotSufficient(item.getId(), item.getName());
        }

        if (line == null) {
            line = new CartUnit();
            line.setCustomer(customer);
            line.setItem(item);
            line.setQuantity(0);
            customer.getCartUnits().add(line);
        }
        line.setQuantity(line.getQuantity() + dto.quantity());

        return buildCart(customer);
    }

    @Transactional
    public CartResponseDto removeItem(Integer customerId, Long itemId) {
        Customer customer = customerService.findCustomerOrThrow(customerId);
        customer.getCartUnits().removeIf(u -> u.getItem().getId().equals(itemId));
        return buildCart(customer);
    }

    @Transactional(readOnly = true)
    public CartResponseDto getCart(Integer customerId) {
        return buildCart(customerService.findCustomerOrThrow(customerId));
    }

    @Transactional
    public void clearCart(Integer customerId) {
        customerService.findCustomerOrThrow(customerId).getCartUnits().clear();
    }

    private CartResponseDto buildCart(Customer customer) {
        List<CartUnit> lines = customer.getCartUnits();

        List<Item> items = lines.stream().map(CartUnit::getItem).toList();
        Map<Long, ItemPricing> pricing = discountPricingService.priceItems(items);

        List<CartItemDto> itemDtos = lines.stream()
                .map(line -> {
                    Item item = line.getItem();
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
}
