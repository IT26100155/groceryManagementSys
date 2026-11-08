package com.grocerymanagementsys.address.repositories;

import com.grocerymanagementsys.address.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Integer> {
    List<Address> findByCustomerId(Integer customerId);
    Optional<Address> findByIdAndCustomerId(Integer id, Integer customerId);
}
