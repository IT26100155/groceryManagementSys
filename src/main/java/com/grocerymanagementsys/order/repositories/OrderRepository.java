package com.grocerymanagementsys.order.repositories;

import com.grocerymanagementsys.order.entities.Order;
<<<<<<< Updated upstream
import org.springframework.data.jpa.repository.JpaRepository;
=======
import com.grocerymanagementsys.payment.enums.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> Stashed changes

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
<<<<<<< Updated upstream
    List<Order> findByCustomerId(Integer customerId);
    boolean existsByAddressId(Integer addressId);
=======

    /** Orders that have at least one payment in the given state, newest first. */
    @Query("select distinct o from Order o join o.payments p where p.state = :state order by o.date desc")
    List<Order> findByPaymentState(@Param("state") PaymentState state);
>>>>>>> Stashed changes
}
