package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.ItemCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    Optional<ItemCategory> findByName(String name);
}
