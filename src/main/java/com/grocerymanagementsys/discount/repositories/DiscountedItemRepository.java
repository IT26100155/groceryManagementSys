package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories;

import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.DiscountedItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface DiscountedItemRepository extends JpaRepository<DiscountedItem, Integer> {
    List<DiscountedItem> findByDiscountId(Integer discountId);
    List<DiscountedItem> findByItemId(Integer itemId);
    List<DiscountedItem> findByDiscountIdIn(Collection<Integer> discountIds);  // one query for many discounts
    void deleteByDiscountId(Integer discountId);
}
