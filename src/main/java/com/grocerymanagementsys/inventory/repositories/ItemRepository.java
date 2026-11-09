package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN i.categories c WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR c.id = :categoryId) " +
            "ORDER BY i.name")
    List<Item> searchItems(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);

    /** Atomic "take stock": returns 0 when there is not enough left, so two buyers can never both get the last unit. */
    @Modifying(flushAutomatically = true)
    @Query("UPDATE Item i SET i.availableStock = i.availableStock - :qty WHERE i.id = :id AND i.availableStock >= :qty")
    int decrementStock(@Param("id") Long id, @Param("qty") int qty);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Item i SET i.availableStock = i.availableStock + :qty WHERE i.id = :id")
    int incrementStock(@Param("id") Long id, @Param("qty") int qty);
}
