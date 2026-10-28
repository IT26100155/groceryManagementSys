package com.grocerymanagementsys.employee.entities;

import com.grocerymanagementsys.common.entities.User;
import com.grocerymanagementsys.employee.enums.EmployeeRole;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
public class Employee extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmployeeRole role;
}
