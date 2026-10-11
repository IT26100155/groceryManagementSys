package com.grocerymanagementsys.cart.repositories;

import com.grocerymanagementsys.cart.entities.CartUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<CartUnit, Integer> {
    List<CartUnit> findByCustomerId(Integer customerId);
    Optional<CartUnit> findByCustomerIdAndItemId(Integer customerId, Integer itemId); //get cart
    void deleteByCustomerId(Integer customerId); //delete cart
}
