package com.mycompany.item.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_update")
public class InventoryUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "employee_id")
    private Long employeeId;

    private int amount;

    // Reason choices: restock, sale, return, correction
    private String reason;

    public InventoryUpdate() {}

    public InventoryUpdate(Item item, Long employeeId, int amount, String reason) {
        this.item = item;
        this.employeeId = employeeId;
        this.amount = amount;
        this.reason = reason;
        this.date = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public LocalDateTime getDate() { return date; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }
    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
