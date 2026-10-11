package com.grocerymanagementsys.common.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class ItemUnit {

    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Column(nullable = false)
    private Integer quantity;
}
