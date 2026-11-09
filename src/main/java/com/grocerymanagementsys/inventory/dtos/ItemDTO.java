package com.grocerymanagementsys.inventory.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {

    private Long id;

    @NotBlank(message = "Item name is required")
    private String name;

    private List<Long> categoryIds = new ArrayList<>();
    private List<String> categoryNames = new ArrayList<>();

    private String description;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer availableStock;

    private String brandSupplier;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be positive")
    private BigDecimal price;

    /** The stored web path of the picture (read side). */
    private String imageUrl;
    /** The picture chosen in the add-item form (write side). */
    private MultipartFile imageFile;
    private String storageType;

    private String itemType;

    private String expirationDate;
    private String storageRequirement;

    private Integer shelfLifeMonths;
}
