package com.grocerymanagementsys.order.entities;

import com.grocerymanagementsys.common.entities.ItemUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

// One row per item inside an order. unitPrice is a SNAPSHOT of Item.price at the
// time of order - never re-read from Item later, so historical orders stay correct
// even if the item's price changes afterward. itemId / quantity are inherited
// from ItemUnit.
@Entity
@Table(name = "order_units")
@Getter
@Setter
@NoArgsConstructor
public class OrderUnit extends ItemUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "order_id", nullable = false)
    private Integer orderId;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
