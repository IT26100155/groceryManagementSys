package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
