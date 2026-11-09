package com.grocerymanagementsys.inventory.services;

import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.discount.dtos.ItemPricing;
import com.grocerymanagementsys.discount.repositories.DiscountRepository;
import com.grocerymanagementsys.discount.services.DiscountPricingService;
import com.grocerymanagementsys.employee.entities.Employee;
import com.grocerymanagementsys.employee.repositories.EmployeeRepository;
import com.grocerymanagementsys.inventory.dtos.InventoryUpdateDTO;
import com.grocerymanagementsys.inventory.dtos.ItemCategoryDTO;
import com.grocerymanagementsys.inventory.dtos.CategorySummaryDto;
import com.grocerymanagementsys.inventory.dtos.ItemDTO;
import com.grocerymanagementsys.inventory.dtos.StoreItemDto;
import com.grocerymanagementsys.inventory.entities.*;
import com.grocerymanagementsys.inventory.repositories.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final ItemRepository itemRepository;
    private final ItemCategoryRepository categoryRepository;
    private final ItemImageRepository imageRepository;
    private final InventoryUpdateRepository updateRepository;
    private final EmployeeRepository employeeRepository;
    private final DiscountRepository discountRepository;
    private final DiscountPricingService discountPricingService;
    private final ImageStorageService imageStorage;

    public Item findItemOrThrow(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item", id));
    }

    @Transactional(readOnly = true)
    public List<ItemDTO> getAllItems(String keyword, Long categoryId) {
        List<Item> items = itemRepository.searchItems(keyword, categoryId);
        return items.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // ---------------------------------------------------------------- storefront (customer) reads

    @Transactional(readOnly = true)
    public List<StoreItemDto> getStoreItems(String keyword, Long categoryId) {
        List<Item> items = itemRepository.searchItems(keyword, categoryId);
        Map<Long, ItemPricing> pricing = discountPricingService.priceItems(items);
        return items.stream().map(i -> toStoreItem(i, pricing.get(i.getId()))).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StoreItemDto getStoreItem(Long id) {
        Item item = findItemOrThrow(id);
        return toStoreItem(item, discountPricingService.priceItem(item));
    }

    private StoreItemDto toStoreItem(Item item, ItemPricing pricing) {
        List<ItemCategory> categories = item.getCategories().stream()
                .sorted(Comparator.comparing(ItemCategory::getName))
                .toList();
        String imageUrl = item.getImages().isEmpty() ? null : item.getImages().get(0).getUrl();
        return new StoreItemDto(
                item.getId(), item.getName(), item.getBrandSupplier(), item.getDescription(),
                item.getPrice(), pricing.discountedPrice(), pricing.discounts(),
                item.getAvailableStock(),
                categories.stream().map(ItemCategory::getId).toList(),
                categories.stream().map(ItemCategory::getName).toList(),
                imageUrl, item.getStorageType());
    }

    // ---------------------------------------------------------------- categories

    public List<ItemCategoryDTO> getAllCategories() {
        return categoryRepository.findAll(Sort.by("name")).stream()
                .map(c -> new ItemCategoryDTO(c.getId(), c.getName()))
                .collect(Collectors.toList());
    }

    public ItemCategoryDTO getCategory(Long id) {
        ItemCategory c = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        return new ItemCategoryDTO(c.getId(), c.getName());
    }

    @Transactional(readOnly = true)
    public List<CategorySummaryDto> getCategorySummaries() {
        return categoryRepository.findAllWithItemCounts();
    }

    @Transactional
    public ItemCategoryDTO createCategory(String rawName) {
        String name = rawName == null ? "" : rawName.trim().replaceAll("\\s+", " ");
        if (name.isEmpty()) {
            throw new ValidationException("Category name is required.");
        }
        if (name.length() > 100) {
            throw new ValidationException("Category name is too long (100 characters max).");
        }
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new ValidationException("A category called \"" + name + "\" already exists.");
        }
        ItemCategory saved = categoryRepository.save(ItemCategory.builder().name(name).build());
        return new ItemCategoryDTO(saved.getId(), saved.getName());
    }

    /** Swaps the item's picture for a newly uploaded one. The old file is removed once the change is saved. */
    @Transactional
    public void replaceItemImage(Long itemId, MultipartFile file) {
        Item item = findItemOrThrow(itemId);
        String stored = imageStorage.store(file);
        if (stored == null) {
            throw new ValidationException("Choose a picture first.");
        }
        List<String> previous = item.getImages().stream().map(ItemImage::getUrl).toList();
        item.getImages().clear();
        item.getImages().add(ItemImage.builder().url(stored).item(item).build());
        deleteFilesAfterCommit(previous);
    }

    @Transactional
    public void removeItemImage(Long itemId) {
        Item item = findItemOrThrow(itemId);
        List<String> previous = item.getImages().stream().map(ItemImage::getUrl).toList();
        item.getImages().clear();
        deleteFilesAfterCommit(previous);
    }

    private void deleteFilesAfterCommit(List<String> webPaths) {
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                webPaths.forEach(imageStorage::delete);
            }
        });
    }

    /** Deletes a category. Items that were filed under it stay in the inventory, just without that category. */
    @Transactional
    public String deleteCategory(Long id) {
        ItemCategory category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", id));
        for (Item item : new ArrayList<>(category.getItems())) {
            item.getCategories().remove(category);
        }
        categoryRepository.delete(category);
        return category.getName();
    }

    /** Replaces the item's categories with exactly the given ones (an empty list leaves it uncategorised). */
    @Transactional
    public void updateItemCategories(Long itemId, List<Long> categoryIds) {
        Item item = findItemOrThrow(itemId);
        Set<ItemCategory> wanted = loadCategories(categoryIds);
        item.getCategories().retainAll(wanted);
        item.getCategories().addAll(wanted);
    }

    @Transactional(readOnly = true)
    public ItemDTO getItemForEdit(Long id) {
        return convertToDTO(findItemOrThrow(id));
    }

    @Transactional
    public Item savePerishableItem(ItemDTO dto) {
        PerishableItem item = new PerishableItem();
        populateBaseItem(item, dto);
        item.setExpirationDate(dto.getExpirationDate());
        item.setStorageRequirement(dto.getStorageRequirement());

        Item saved = itemRepository.save(item);
        saveImageIfPresent(saved, imageStorage.store(dto.getImageFile()));
        return saved;
    }

    @Transactional
    public Item saveNonPerishableItem(ItemDTO dto) {
        NonPerishableItem item = new NonPerishableItem();
        populateBaseItem(item, dto);
        item.setShelfLifeMonths(dto.getShelfLifeMonths());

        Item saved = itemRepository.save(item);
        saveImageIfPresent(saved, imageStorage.store(dto.getImageFile()));
        return saved;
    }

    private void populateBaseItem(Item item, ItemDTO dto) {
        item.setName(dto.getName());
        item.setDescription(dto.getDescription());
        item.setAvailableStock(dto.getAvailableStock() != null ? dto.getAvailableStock() : 0);
        item.setBrandSupplier(dto.getBrandSupplier());
        item.setPrice(dto.getPrice());
        item.setCategories(loadCategories(dto.getCategoryIds()));
    }

    private Set<ItemCategory> loadCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Long> wanted = new HashSet<>(categoryIds);
        List<ItemCategory> found = categoryRepository.findAllById(wanted);
        if (found.size() != wanted.size()) {
            Set<Long> foundIds = found.stream().map(ItemCategory::getId).collect(Collectors.toSet());
            Long missing = wanted.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Category", missing);
        }
        return new HashSet<>(found);
    }

    private void saveImageIfPresent(Item item, String storedPath) {
        if (storedPath == null) {
            return;
        }
        try {
            ItemImage image = ItemImage.builder()
                    .url(storedPath)
                    .item(item)
                    .build();
            imageRepository.save(image);
            item.getImages().add(image);
        } catch (RuntimeException e) {
            imageStorage.delete(storedPath);
            throw e;
        }
    }

    @Transactional
    public void performQuickStockUpdate(InventoryUpdateDTO dto) {
        Item item = itemRepository.findById(dto.getItemId())
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        Employee employee = employeeRepository.findById(dto.getEmployeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));

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
                .employee(employee)
                .amount(change)
                .reason(dto.getReason())
                .build();

        updateRepository.save(updateLog);
    }

    @Transactional
    public void deleteItem(Long id) {
        List<String> pictures = imageRepository.findByItemId(id).stream().map(ItemImage::getUrl).toList();
        discountRepository.deleteItemLinks(id);
        itemRepository.deleteById(id);
        pictures.forEach(imageStorage::delete);
    }

    @Transactional(readOnly = true)
    public List<InventoryUpdateDTO> getAllAuditLogs() {
        return updateRepository.findAllByOrderByDateDesc().stream().map(log -> {
            InventoryUpdateDTO dto = new InventoryUpdateDTO();
            dto.setId(log.getId());
            dto.setDate(log.getDate());
            dto.setItemId(log.getItem().getId());
            dto.setItemName(log.getItem().getName());
            dto.setEmployeeId(log.getEmployee().getId());
            dto.setAmount(log.getAmount());
            dto.setReason(log.getReason());
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public void writeItemsToCsv(PrintWriter writer) {
        writer.println("ID,Name,Categories,Storage Type,Brand/Supplier,Price,Stock");
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            String categories = item.getCategories().isEmpty() ? "N/A" : joinCategoryNames(item);
            writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",%.2f,%d%n",
                    item.getId(),
                    escapeCsv(item.getName()),
                    escapeCsv(categories),
                    escapeCsv(item.getStorageType()),
                    escapeCsv(item.getBrandSupplier()),
                    item.getPrice(),
                    item.getAvailableStock());
        }
    }

    private String joinCategoryNames(Item item) {
        return item.getCategories().stream()
                .map(ItemCategory::getName)
                .sorted()
                .collect(Collectors.joining("; "));
    }

    private String escapeCsv(String input) {
        return input == null ? "" : input.replace("\"", "\"\"");
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());

        List<ItemCategory> categories = item.getCategories().stream()
                .sorted(Comparator.comparing(ItemCategory::getName))
                .toList();
        dto.setCategoryIds(categories.stream().map(ItemCategory::getId).collect(Collectors.toList()));
        dto.setCategoryNames(categories.stream().map(ItemCategory::getName).collect(Collectors.toList()));

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
