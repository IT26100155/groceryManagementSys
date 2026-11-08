package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("SELECT i FROM Item i WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR i.category.id = :categoryId)")
    List<Item> searchItems(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}
