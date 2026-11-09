package com.grocerymanagementsys.inventory.repositories;

import com.grocerymanagementsys.inventory.entities.InventoryUpdate;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryUpdateRepository extends JpaRepository<InventoryUpdate, Long> {
    @EntityGraph(attributePaths = {"item", "employee"})
    List<InventoryUpdate> findAllByOrderByDateDesc();

    List<InventoryUpdate> findByItemIdOrderByDateDesc(Long itemId);
    List<InventoryUpdate> findByEmployeeIdOrderByDateDesc(Integer employeeId);
}
