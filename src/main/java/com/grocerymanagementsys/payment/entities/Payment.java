package com.grocerymanagementsys.payment.entities;

import com.grocerymanagementsys.order.entities.Order;
import com.grocerymanagementsys.payment.enums.PaymentState;
import com.grocerymanagementsys.payment.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentType type;

    private LocalDateTime date;

    private LocalDateTime expiryTime;

    @Enumerated(EnumType.STRING)
    private PaymentState state;
}
