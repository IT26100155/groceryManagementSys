package com.grocerymanagementsys.inventory.services;

import com.grocerymanagementsys.inventory.dtos.InventoryUpdateDTO;
import com.grocerymanagementsys.inventory.dtos.ItemCategoryDTO;
import com.grocerymanagementsys.inventory.dtos.ItemDTO;
import com.grocerymanagementsys.inventory.entities.*;
import com.grocerymanagementsys.inventory.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;
    private final ItemImageRepository imageRepository;
    private final InventoryUpdateRepository updateRepository;

    public List<ItemDTO> getAllItems(String keyword, Long categoryId) {
        List<Item> items = itemRepository.searchItems(keyword, categoryId);
        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public List<ItemCategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(c -> new ItemCategoryDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Item savePerishableItem(ItemDTO dto) {
        PerishableItem item = new PerishableItem();
        populateBaseItem(item, dto);
        item.setExpirationDate(dto.getExpirationDate());
        item.setStorageRequirement(dto.getStorageRequirement());

        Item saved = itemRepository.save(item);
        saveImageIfPresent(saved, dto.getImageUrl());
        return saved;
    }

    @Transactional
    public Item saveNonPerishableItem(ItemDTO dto) {
        NonPerishableItem item = new NonPerishableItem();
        populateBaseItem(item, dto);
        item.setShelfLifeMonths(dto.getShelfLifeMonths());

        Item saved = itemRepository.save(item);
        saveImageIfPresent(saved, dto.getImageUrl());
        return saved;
    }

    private void populateBaseItem(Item item, ItemDTO dto) {
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailableStock(dto.getAvailableStock() != null ? dto.getAvailableStock() : 0);
        item.setBrandSupplier(dto.getBrandSupplier());
        item.setPrice(dto.getPrice());

        if (dto.getCategoryId() != null) {
            ItemCategory category = categoryRepository.findById(dto.getCategoryId())
                    .orElse(null);
            item.setCategory(category);
        }
    }

    private void saveImageIfPresent(Item item, String imageUrl) {
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ItemImage image = ItemImage.builder()
                    .url(imageUrl)
                    .item(item)
                    .build();
            imageRepository.save(image);
            item.getImages().add(image);
        }
    }

    @Transactional
    public void performQuickStockUpdate(InventoryUpdateDTO dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));

        int change = dto.getAmount();
        if ("Decrease".equalsIgnoreCase(dto.getUpdateType())) {
            change = -Math.abs(change);
        } else {
            change = Math.abs(change);
        }

        int newStock = item.getAvailableStock() + change;
        if (newStock < 0) {
            throw new IllegalArgumentException("Insufficient stock available");
        }

        item.setAvailableStock(newStock);
        itemRepository.save(item);

        InventoryUpdate updateLog = InventoryUpdate.builder()
                .date(LocalDateTime.now())
                .item(item)
                .employeeId(dto.getEmployeeId())
                .amount(change)
                .reason(dto.getReason())
                .build();

        updateRepository.save(updateLog);
    }

    @Transactional
    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<InventoryUpdateDTO> getAllAuditLogs() {
        return updateRepository.findAllByOrderByDateDesc().stream().map(log -> {
            InventoryUpdateDTO dto = new InventoryUpdateDTO();
            dto.setId(log.getId());
            dto.setDate(log.getDate());
            dto.setItemId(log.getItem().getId());
            dto.setItemName(log.getItem().getName());
            dto.setEmployeeId(log.getEmployeeId());
            dto.setAmount(log.getAmount());
            dto.setReason(log.getReason());
            return dto;
        }).collect(Collectors.toList());
    }

    public void writeItemsToCsv(PrintWriter writer) {
        writer.println("ID,Name,Category,Storage Type,Brand/Supplier,Price,Stock");
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            String category = item.getCategory() != null ? item.getCategory().getName() : "N/A";
            writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%d%n",
                    item.getId(),
                    escapeCsv(item.getName()),
                    escapeCsv(category),
                    escapeCsv(item.getStorageType()),
                    escapeCsv(item.getBrandSupplier()),
                    item.getPrice(),
                    item.getAvailableStock());
        }
    }

    private String escapeCsv(String input) {
        return input == null ? "" : input.replace("\"", "\"\"");
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        if (item.getCategory() != null) {
            dto.setCategoryId(item.getCategory().getId());
            dto.setCategoryName(item.getCategory().getName());
        }
        dto.setDescription(item.getDescription());
        dto.setAvailableStock(item.getAvailableStock());
        dto.setBrandSupplier(item.getBrandSupplier());
        dto.setPrice(item.getPrice());
        dto.setStorageType(item.getStorageType());

        if (item.getImages() != null && !item.getImages().isEmpty()) {
            dto.setImageUrl(item.getImages().get(0).getUrl());
        } else {
            dto.setImageUrl("https://via.placeholder.com/60");
        }

        if (item instanceof PerishableItem) {
            PerishableItem p = (PerishableItem) item;
            dto.setItemType("PERISHABLE");
            dto.setExpirationDate(p.getExpirationDate());
            dto.setStorageRequirement(p.getStorageRequirement());
        } else if (item instanceof NonPerishableItem) {
            NonPerishableItem np = (NonPerishableItem) item;
            dto.setItemType("NON_PERISHABLE");
            dto.setShelfLifeMonths(np.getShelfLifeMonths());
        }

        return dto;
    }
}
