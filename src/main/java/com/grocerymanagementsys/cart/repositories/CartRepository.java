package com.grocerymanagementsys.cart.repositories;

import com.grocerymanagementsys.cart.entities.CartUnit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<CartUnit, Integer> { }
