package com.grocerymanagementsys.payment.repositories;

import com.grocerymanagementsys.payment.entities.Payment;
import com.grocerymanagementsys.payment.enums.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByOrderId(Integer orderId);
    boolean existsByOrderId(Integer orderId);
    boolean existsByOrderIdAndState(Integer orderId, PaymentState state);
}
