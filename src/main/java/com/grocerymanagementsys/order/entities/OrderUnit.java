package com.grocerymanagementsys.order.entities;

import com.grocerymanagementsys.common.entities.ItemUnit;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Column(name = "unit_price", nullable = false)
    private BigDecimal unitPrice;
}
