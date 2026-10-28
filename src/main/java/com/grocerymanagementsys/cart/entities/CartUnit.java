package com.grocerymanagementsys.cart.entities;

import com.grocerymanagementsys.common.entities.ItemUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "cart_units")
@Getter
@Setter
@NoArgsConstructor
public class CartUnit extends ItemUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "customer_id", nullable = false)
    private Integer customerId;
}
