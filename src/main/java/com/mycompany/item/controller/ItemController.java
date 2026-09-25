package com.mycompany.item.controller;

import com.mycompany.item.model.Category;
import com.mycompany.item.model.Item;
import com.mycompany.item.model.ItemImage;
import com.mycompany.item.repository.CategoryRepository;
import com.mycompany.item.repository.InventoryUpdateRepository;
import com.mycompany.item.repository.ItemRepository;
import com.mycompany.item.service.ItemService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class ItemController {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final InventoryUpdateRepository inventoryUpdateRepository;
    private final ItemService itemService;

    public ItemController(ItemRepository itemRepository,
                          CategoryRepository categoryRepository,
                          InventoryUpdateRepository inventoryUpdateRepository,
                          ItemService itemService) {
        this.itemRepository = itemRepository;
        this.categoryRepository = categoryRepository;
        this.inventoryUpdateRepository = inventoryUpdateRepository;
        this.itemService = itemService;
    }

    // Default Redirect to Catalog
    @GetMapping("/")
    public String index() {
        return "redirect:/catalog";
    }

    // 1. Product Catalog / Search & Filter Page
    @GetMapping("/catalog")
    public String showCatalog(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) Long categoryId,
                              @RequestParam(required = false) Double maxPrice,
                              Model model) {
        model.addAttribute("items", itemRepository.filterItems(keyword, categoryId, maxPrice));
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("maxPrice", maxPrice);
        return "catalog";
    }

    // 2. Product Detail Page
    @GetMapping("/product/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid item ID: " + id));
        model.addAttribute("item", item);
        return "product_detail";
    }

    // 3. Show Form to Create New Item
    @GetMapping("/product/new")
    public String showCreateItemForm(Model model) {
        model.addAttribute("item", new Item());
        model.addAttribute("categories", categoryRepository.findAll());
        return "add_item";
    }

    // 4. Save New Item Action
    @PostMapping("/product/save")
    public String saveItem(@ModelAttribute("item") Item item,
                           @RequestParam(required = false) Long categoryId,
                           @RequestParam(required = false) String imageUrl) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId).orElse(null);
            item.setCategory(category);
        }

        Item savedItem = itemRepository.save(item);

        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            ItemImage image = new ItemImage(imageUrl, savedItem);
            savedItem.getImages().add(image);
            itemRepository.save(savedItem);
        }

        if (savedItem.getAvailableStock() > 0) {
            itemService.updateStock(savedItem.getId(), 0, "restock", 1001L);
        }

        return "redirect:/catalog";
    }

    // 5. Staff Inventory Dashboard (View)
    @GetMapping("/staff/inventory")
    public String showInventoryDashboard(Model model) {
        model.addAttribute("items", itemRepository.findAll());
        model.addAttribute("logs", inventoryUpdateRepository.findAll());
        return "inventory_dashboard";
    }

    // 6. Staff Inventory Dashboard (Stock Update Action)
    @PostMapping("/staff/inventory/update")
    public String updateInventory(@RequestParam Long itemId,
                                  @RequestParam int amount,
                                  @RequestParam String reason,
                                  @RequestParam Long employeeId) {
        itemService.updateStock(itemId, amount, reason, employeeId);
        return "redirect:/staff/inventory";
    }

    // 7. FILE HANDLING: Export Inventory Data to CSV File
    @GetMapping("/staff/inventory/export-csv")
    public void exportInventoryToCSV(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"inventory_report.csv\"");

        PrintWriter writer = response.getWriter();

        // CSV Header Row
        writer.println("Item ID,Name,Brand,Category,Price,Available Stock");

        // Loop through entities and write to CSV stream
        for (Item item : itemRepository.findAll()) {
            String categoryName = (item.getCategory() != null) ? item.getCategory().getName() : "Uncategorized";
            writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",%.2f,%d",
                    item.getId(),
                    item.getName(),
                    item.getBrandSupplier(),
                    categoryName,
                    item.getPrice(),
                    item.getAvailableStock()
            ));
        }

        writer.flush();
        writer.close();
    }
}
