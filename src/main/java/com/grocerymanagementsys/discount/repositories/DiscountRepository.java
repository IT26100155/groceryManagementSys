package com.grocerymanagementsys.discount.repositories;

import com.grocerymanagementsys.discount.entities.Discount;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    @EntityGraph(attributePaths = "items")
    List<Discount> findByActiveTrue();

    @Query("SELECT DISTINCT d FROM Discount d LEFT JOIN FETCH d.items WHERE d.active = true AND d.startDate <= :today AND d.expireDate >= :today")
    List<Discount> findCurrentlyValid(@Param("today") LocalDate today);

    @Modifying
    @Query(value = "DELETE FROM discounted_items WHERE item_id = :itemId", nativeQuery = true)
    void deleteItemLinks(@Param("itemId") Long itemId);
}
