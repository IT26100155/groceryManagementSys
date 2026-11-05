package com.grocerymanagementsys.discount.services;

import com.grocerymanagementsys.discount.dtos.ItemDiscountDto;
import com.grocerymanagementsys.discount.dtos.ItemPricing;
import com.grocerymanagementsys.discount.entities.Discount;
import com.grocerymanagementsys.discount.repositories.DiscountRepository;
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

@Service
@RequiredArgsConstructor
public class DiscountPricingService {
    private final DiscountRepository discountRepository;

    public ItemPricing priceItem(Item item) {
        return priceItems(List.of(item)).get(item.getId());
    }

    public Map<Long, ItemPricing> priceItems(Collection<Item> items) {
        List<Discount> valid = discountRepository.findCurrentlyValid(LocalDate.now());
        Map<Integer, Set<Long>> itemIdsByDiscount = scopedItemIds(valid);

        Map<Long, ItemPricing> result = new HashMap<>();
        for (Item item : items) {
            List<Discount> applicable = valid.stream()
                    .filter(d -> appliesTo(d, item, itemIdsByDiscount))
                    .sorted(Comparator.comparing(Discount::getPercentage).reversed())
                    .toList();
            result.put(item.getId(), toPricing(item, applicable));
        }
        return result;
    }

    private Map<Integer, Set<Long>> scopedItemIds(List<Discount> valid) {
        return valid.stream()
                .filter(d -> !d.isStoreWide())
                .collect(Collectors.toMap(
                        Discount::getId,
                        d -> d.getItems().stream().map(Item::getId).collect(Collectors.toSet())));
    }

    private boolean appliesTo(Discount discount, Item item, Map<Integer, Set<Long>> itemIdsByDiscount) {
        boolean covers = discount.isStoreWide() || itemIdsByDiscount.getOrDefault(discount.getId(), Set.of()).contains(item.getId());
        boolean meetsMinimum = discount.getMinPrice() == null || item.getPrice().compareTo(discount.getMinPrice()) >= 0;
        return covers && meetsMinimum;
    }

    private ItemPricing toPricing(Item item, List<Discount> applicable) {
        List<ItemDiscountDto> discounts = applicable.stream()
                .map(d -> new ItemDiscountDto(d.getId(), d.getPercentage(), d.getExpireDate()))
                .toList();

        BigDecimal discountedPrice;

        if (applicable.isEmpty()) {
            discountedPrice = item.getPrice().setScale(2, RoundingMode.HALF_UP);
        }

        else {
            int best = applicable.get(0).getPercentage();
            discountedPrice = item.getPrice()
                    .multiply(BigDecimal.valueOf(100 - best))
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }
        return new ItemPricing(discounts, discountedPrice);
    }
}
