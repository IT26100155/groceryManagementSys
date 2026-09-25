package com.mycompany.item.service;

import com.mycompany.item.model.*;
import com.mycompany.item.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final InventoryUpdateRepository inventoryUpdateRepository;

    public ItemService(ItemRepository itemRepository, InventoryUpdateRepository inventoryUpdateRepository) {
        this.itemRepository = itemRepository;
        this.inventoryUpdateRepository = inventoryUpdateRepository;
    }

    @Transactional
    public void updateStock(Long itemId, int amount, String reason, Long employeeId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found: " + itemId));

        // Adjust available stock
        item.setAvailableStock(item.getAvailableStock() + amount);
        itemRepository.save(item);

        // Record inventory update log
        InventoryUpdate updateLog = new InventoryUpdate(item, employeeId, amount, reason);
        inventoryUpdateRepository.save(updateLog);
    }
}
