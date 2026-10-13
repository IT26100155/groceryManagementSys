package com.grocerymanagementsys.inventory.controller;

import com.grocerymanagementsys.inventory.dtos.InventoryUpdateDTO;
import com.grocerymanagementsys.inventory.dtos.ItemDTO;
import com.grocerymanagementsys.inventory.services.InventoryService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping("/items")
    public String listItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            Model model) {
        model.addAttribute("items", inventoryService.getAllItems(keyword, categoryId));
        model.addAttribute("categories", inventoryService.getAllCategories());
        model.addAttribute("selectedCategory", categoryId);
        model.addAttribute("keyword", keyword);
        return "inventory/items-list";
    }

    @GetMapping("/add-perishable")
    public String showAddPerishableForm(Model model) {
        model.addAttribute("itemDTO", new ItemDTO());
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "inventory/add-perishable";
    }

    @PostMapping("/add-perishable")
    public String savePerishableItem(@ModelAttribute("itemDTO") ItemDTO dto, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.savePerishableItem(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Perishable item added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + e.getMessage());
        }
        return "redirect:/api/inventory/items";
    }

    @GetMapping("/add-non-perishable")
    public String showAddNonPerishableForm(Model model) {
        model.addAttribute("itemDTO", new ItemDTO());
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "inventory/add-non-perishable";
    }

    @PostMapping("/add-non-perishable")
    public String saveNonPerishableItem(@ModelAttribute("itemDTO") ItemDTO dto, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.saveNonPerishableItem(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Non-Perishable item added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + e.getMessage());
        }
        return "redirect:/api/inventory/items";
    }

    @PostMapping("/quick-update")
    public String quickUpdateStock(@ModelAttribute InventoryUpdateDTO dto, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.performQuickStockUpdate(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + e.getMessage());
        }
        return "redirect:/api/inventory/items";
    }

    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete item: " + e.getMessage());
        }
        return "redirect:/api/inventory/items";
    }

    @GetMapping("/audit-logs")
    public String viewAuditLogs(Model model) {
        model.addAttribute("logs", inventoryService.getAllAuditLogs());
        return "inventory/audit-logs";
    }

    @GetMapping("/export-csv")
    public void exportToCsv(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=grocery_inventory.csv");
        inventoryService.writeItemsToCsv(response.getWriter());
    }
}
