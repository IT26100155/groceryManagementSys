package com.mycompany.item.repository;

import com.mycompany.item.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Search and Filter query
    @Query("SELECT i FROM Item i WHERE " +
            "(:keyword IS NULL OR LOWER(i.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(i.brandSupplier) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR i.category.id = :categoryId) AND " +
            "(:maxPrice IS NULL OR i.price <= :maxPrice)")
    List<Item> filterItems(@Param("keyword") String keyword,
                           @Param("categoryId") Long categoryId,
                           @Param("maxPrice") Double maxPrice);
}
