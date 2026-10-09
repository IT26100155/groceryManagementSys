package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(nullable = false)
    private Integer percentage;

    @Column(name = "min_price")
    private java.math.BigDecimal minPrice;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "expire_date", nullable = false)
    private LocalDate expireDate;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "employee_id", nullable = false)
    private Integer employeeId;

    @Column(name = "is_store_wide", nullable = false)
    private boolean storeWide;

    // NOTE: which items a non-store-wide discount applies to is a many-to-many
    // relationship (the "discountedItems" join table from the schema) - left out
    // here for now. Add a DiscountedItem join entity + repository when item-scoped
    // discounts are needed; for now storeWide=true is the only supported case.
}
