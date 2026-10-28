package com.grocerymanagementsys.order.entities;

<<<<<<< Updated upstream
import com.grocerymanagementsys.order.enums.OrderStatus;
=======
import com.grocerymanagementsys.address.entities.Address;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.payment.entities.Payment;
>>>>>>> Stashed changes
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
<<<<<<< Updated upstream
=======
import java.util.ArrayList;
import java.util.List;
>>>>>>> Stashed changes

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
<<<<<<< Updated upstream
@AllArgsConstructor
=======
>>>>>>> Stashed changes
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

<<<<<<< Updated upstream
    @Column(name = "customer_id", nullable = false)
    private Integer customerId;

    @Column(name = "address_id")
    private Integer addressId;
=======
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;
>>>>>>> Stashed changes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Column(nullable = false)
    private BigDecimal total;

    @Column(nullable = false)
    private LocalDateTime date;
<<<<<<< Updated upstream
=======

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderUnit> orderUnits = new ArrayList<>();

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<Payment> payments = new ArrayList<>();
>>>>>>> Stashed changes
}
