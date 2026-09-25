package com.mycompany.item.repository;

import com.mycompany.item.model.InventoryUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryUpdateRepository extends JpaRepository<InventoryUpdate, Long> {
    List<InventoryUpdate> findByItemIdOrderByDateDesc(Long itemId);
}
