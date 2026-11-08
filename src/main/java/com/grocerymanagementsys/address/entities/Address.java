package com.grocerymanagementsys.address.entities;

import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.order.entities.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "address_body", nullable = false, length = 500)
    private String addressBody;

    @OneToMany(mappedBy = "address", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
