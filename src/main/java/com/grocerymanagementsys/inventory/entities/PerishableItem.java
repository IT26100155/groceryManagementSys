package com.grocerymanagementsys.inventory.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("PERISHABLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PerishableItem extends Item {

    private String expirationDate;
    private String storageRequirement;

    @Override
    public String getStorageType() {
        return "Perishable (" + (storageRequirement != null && !storageRequirement.trim().isEmpty() ? storageRequirement : "Cold Storage") + ")";
    }
}
