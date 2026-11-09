package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.ItemCategory;
import com.grocerymanagementsys.inventory.dtos.CategorySummaryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemCategoryRepository extends JpaRepository<ItemCategory, Long> {
    Optional<ItemCategory> findByName(String name);

    boolean existsByNameIgnoreCase(String name);

    @Query("SELECT new com.grocerymanagementsys.inventory.dtos.CategorySummaryDto(c.id, c.name, COUNT(i)) " +
            "FROM ItemCategory c LEFT JOIN c.items i GROUP BY c.id, c.name ORDER BY c.name")
    List<CategorySummaryDto> findAllWithItemCounts();
}
