package com.grocerymanagementsys.item.repository;

import com.grocerymanagementsys.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByCategoryId(Long categoryId);

    @Query("SELECT i FROM Item i WHERE " +
            "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR i.category.id = :categoryId)")
    List<Item> searchItems(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
}
