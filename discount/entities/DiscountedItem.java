package com.grocerymanagementsys.discount.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Join table: which items a (non store-wide) discount applies to.
// Same shape as CategorizedItem - plain id + unique pair.
@Entity
@Table(name = "discounted_items",
        uniqueConstraints = @UniqueConstraint(columnNames = {"discount_id", "item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class DiscountedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "discount_id", nullable = false)
    private Integer discountId;

    @Column(name = "item_id", nullable = false)
    private Integer itemId;
}
