package com.grocerymanagementsys.inventory.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    private Long categoryId;
    private String categoryName;

    private String description;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer availableStock;

    private String brandSupplier;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private Double price;

    private String imageUrl;
    private String storageType;

    private String itemType;

    private String expirationDate;
    private String storageRequirement;

    private Integer shelfLifeMonths;
}
