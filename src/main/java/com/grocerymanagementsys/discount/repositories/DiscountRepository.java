package com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories;

import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {
    List<Discount> findByActiveTrue();
    List<Discount> findByExpireDateBeforeAndActiveTrue(LocalDate date); // used by the expiry sweep

    // Active AND inside its date window (both ends inclusive).
    @Query("SELECT d FROM Discount d WHERE d.active = true " +
           "AND d.startDate <= :today AND d.expireDate >= :today")
    List<Discount> findCurrentlyValid(@Param("today") LocalDate today);
}
