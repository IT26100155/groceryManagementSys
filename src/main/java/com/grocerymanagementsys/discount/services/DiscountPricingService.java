package com.grocerymanagementsys.discount.services;

import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos.ItemDiscountDto;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos.ItemPricing;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.Discount;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.DiscountedItem;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories.DiscountRepository;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories.DiscountedItemRepository;
import com.grocerymanagementsys.inventory.entities.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// The ONE place that decides which discounts apply to an item and what it costs
// after discount. ItemService (catalog) and CartService both call this, so the
// catalog price and the cart price can never disagree.
//
// Rules (change them here and everything follows):
//   1. A discount is valid if it is active and today is within start..expire (inclusive).
//   2. It applies to an item if it is store-wide OR the item is linked to it,
//      AND the item's price is at least the discount's minPrice (when set).
//   3. All applicable discounts are listed, highest percentage first.
//   4. Only the BEST one is applied to the price - discounts do not stack.
@Service
@RequiredArgsConstructor
public class DiscountPricingService {
    private final DiscountRepository discountRepository;
    private final DiscountedItemRepository discountedItemRepository;

    public ItemPricing priceItem(Item item) {
        return priceItems(List.of(item)).get(item.getId());
    }

    // Prices many items with a fixed number of queries (2), not one set per item.
    public Map<Integer, ItemPricing> priceItems(Collection<Item> items) {
        List<Discount> valid = discountRepository.findCurrentlyValid(LocalDate.now());
        Map<Integer, Set<Integer>> itemIdsByDiscount = loadScopedItemIds(valid);

        Map<Integer, ItemPricing> result = new HashMap<>();
        for (Item item : items) {
            List<Discount> applicable = valid.stream()
                    .filter(d -> appliesTo(d, item, itemIdsByDiscount))
                    .sorted(Comparator.comparing(Discount::getPercentage).reversed())
                    .toList();
            result.put(item.getId(), toPricing(item, applicable));
        }
        return result;
    }

    // discountId -> ids of the items it is linked to (only for non store-wide discounts).
    private Map<Integer, Set<Integer>> loadScopedItemIds(List<Discount> valid) {
        List<Integer> scopedDiscountIds = valid.stream()
                .filter(d -> !d.isStoreWide())
                .map(Discount::getId)
                .toList();
        if (scopedDiscountIds.isEmpty()) {
            return Map.of();
        }
        return discountedItemRepository.findByDiscountIdIn(scopedDiscountIds).stream()
                .collect(Collectors.groupingBy(
                        DiscountedItem::getDiscountId,
                        Collectors.mapping(DiscountedItem::getItemId, Collectors.toSet())));
    }

    private boolean appliesTo(Discount discount, Item item, Map<Integer, Set<Integer>> itemIdsByDiscount) {
        boolean covers = discount.isStoreWide()
                || itemIdsByDiscount.getOrDefault(discount.getId(), Set.of()).contains(item.getId());
        boolean meetsMinimum = discount.getMinPrice() == null
                || item.getPrice().compareTo(discount.getMinPrice()) >= 0;
        return covers && meetsMinimum;
    }

    private ItemPricing toPricing(Item item, List<Discount> applicable) {
        List<ItemDiscountDto> discounts = applicable.stream()
                .map(d -> new ItemDiscountDto(d.getId(), d.getPercentage(), d.getExpireDate()))
                .toList();

        BigDecimal discountedPrice;
        if (applicable.isEmpty()) {
            discountedPrice = item.getPrice().setScale(2, RoundingMode.HALF_UP);
        } else {
            int best = applicable.get(0).getPercentage();   // list is sorted, best first
            discountedPrice = item.getPrice()
                    .multiply(BigDecimal.valueOf(100 - best))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return new ItemPricing(discounts, discountedPrice);
    }
}
