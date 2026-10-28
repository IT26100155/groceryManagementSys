package com.grocerymanagementsys.customer.entities;

import com.grocerymanagementsys.common.entities.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private Integer preference;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
}
