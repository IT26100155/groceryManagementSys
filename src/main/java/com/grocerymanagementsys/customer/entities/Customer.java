package com.grocerymanagementsys.customer.entities;

import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.cart.entities.CartUnit;
import com.grocerymanagementsys.common.entities.User;
import com.grocerymanagementsys.order.entities.Order;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Getter
@Setter
@NoArgsConstructor
public class Customer extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Address> addresses = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartUnit> cartUnits = new ArrayList<>();

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();
}
