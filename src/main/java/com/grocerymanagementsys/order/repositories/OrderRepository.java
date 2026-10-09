package com.grocerymanagementsys.order.repositories;

import com.grocerymanagementsys.order.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByCustomerId(Integer customerId);
    boolean existsByAddressId(Integer addressId);
}
