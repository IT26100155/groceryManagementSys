package com.grocerymanagementsys.inventory.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("NON_PERISHABLE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class NonPerishableItem extends Item {

    private Integer shelfLifeMonths;

    @Override
    public String getStorageType() {
        return "Non-Perishable";
    }
}
