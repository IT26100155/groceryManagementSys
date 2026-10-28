package com.grocerymanagementsys.inventory.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryUpdateDTO {

    private Long id;
    private LocalDateTime date;

    @NotNull(message = "Item ID is required")
    private Long itemId;

    private String itemName;

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Amount is required")
    private Integer amount;

    private String updateType;

    @NotNull(message = "Reason is required")
    private String reason;
}
