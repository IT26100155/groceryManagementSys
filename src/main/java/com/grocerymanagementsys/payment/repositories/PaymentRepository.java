package com.grocerymanagementsys.payment.repositories;

import com.grocerymanagementsys.payment.entities.Payment;
import com.grocerymanagementsys.payment.enums.PaymentState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByState(PaymentState state);
}
