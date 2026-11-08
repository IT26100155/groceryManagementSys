package com.grocerymanagementsys.cart.entities;

import com.grocerymanagementsys.common.entities.ItemUnit;
import com.grocerymanagementsys.customer.entities.Customer;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_units", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "item_id"}))
@Getter
@Setter
@NoArgsConstructor
public class CartUnit extends ItemUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
