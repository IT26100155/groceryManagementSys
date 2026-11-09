package com.grocerymanagementsys.inventory.controller;

import com.grocerymanagementsys.inventory.dtos.ItemCategoryDTO;
import com.grocerymanagementsys.inventory.dtos.StoreItemDto;
import com.grocerymanagementsys.inventory.services.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {
    private final InventoryService inventoryService;

    @GetMapping("/items")
    public ResponseEntity<List<StoreItemDto>> items(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(inventoryService.getStoreItems(keyword, categoryId));
    }

    @GetMapping("/items/{id}")
    public ResponseEntity<StoreItemDto> item(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getStoreItem(id));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<ItemCategoryDTO>> categories() {
        return ResponseEntity.ok(inventoryService.getAllCategories());
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<ItemCategoryDTO> category(@PathVariable Long id) {
        return ResponseEntity.ok(inventoryService.getCategory(id));
    }
}
