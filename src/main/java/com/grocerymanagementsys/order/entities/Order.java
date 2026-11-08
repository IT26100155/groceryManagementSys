package com.grocerymanagementsys.order.entities;

import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.payment.entities.Payment;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime date;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderUnit> orderUnits = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
}
