package com.grocerymanagementsys.item.repository;

import com.grocerymanagementsys.item.model.InventoryUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryUpdateRepository extends JpaRepository<InventoryUpdate, Long> {
    List<InventoryUpdate> findByItemIdOrderByDateDesc(Long itemId);
}

