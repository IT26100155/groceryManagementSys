package com.grocerymanagementsys.inventory.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_update")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    @ToString.Exclude
    private Item item;

    @Column(name = "employee_id", nullable = false)
    private Long employeeId;

    @Column(nullable = false)
    private Integer amount;

    @Column(nullable = false)
    private String reason;
}
