<<<<<<< Updated upstream
package com.grocerymanagementsys.order.repositories;

import com.grocerymanagementsys.order.entities.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderUnitRepository extends JpaRepository<OrderUnit, Integer> {
    List<OrderUnit> findByOrderId(Integer orderId);
}
=======
package com.grocerymanagementsys.order.repositories;

import com.grocerymanagementsys.order.entities.OrderUnit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderUnitRepository extends JpaRepository<OrderUnit, Integer> {
    List<OrderUnit> findByOrderId(Integer orderId);
}
>>>>>>> Stashed changes
