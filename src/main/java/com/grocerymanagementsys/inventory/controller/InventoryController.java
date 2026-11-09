package com.grocerymanagementsys.inventory.controller;

import com.grocerymanagementsys.inventory.dtos.InventoryUpdateDTO;
import com.grocerymanagementsys.inventory.dtos.ItemCategoryDTO;
import com.grocerymanagementsys.inventory.dtos.ItemDTO;
import com.grocerymanagementsys.inventory.services.InventoryService;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import com.grocerymanagementsys.staff.interfaces.AdminOnly;
import com.grocerymanagementsys.staff.utility.StaffSession;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/staff/inventory")
@RequiredArgsConstructor
public class InventoryController {
    private static final String LIST = "redirect:/staff/inventory/items";
    private static final String CATEGORIES = "redirect:/staff/inventory/categories";
    private final InventoryService inventoryService;

    @GetMapping({"", "/"})
    public String home() {
        return LIST;
    }

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

    @AdminOnly
    @GetMapping("/add-perishable")
    public String showAddPerishableForm(Model model) {
        model.addAttribute("itemDTO", new ItemDTO());
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "inventory/add-perishable";
    }

    @AdminOnly
    @PostMapping("/add-perishable")
    public String savePerishableItem(@Valid @ModelAttribute("itemDTO") ItemDTO dto, BindingResult binding, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return rejectForm(binding, redirectAttributes, "redirect:/staff/inventory/add-perishable");
        }
        try {
            inventoryService.savePerishableItem(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Perishable item added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + StaffMessages.friendly(e, "please check the form and try again."));
        }
        return LIST;
    }

    @AdminOnly
    @GetMapping("/add-non-perishable")
    public String showAddNonPerishableForm(Model model) {
        model.addAttribute("itemDTO", new ItemDTO());
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "inventory/add-non-perishable";
    }

    @AdminOnly
    @PostMapping("/add-non-perishable")
    public String saveNonPerishableItem(@Valid @ModelAttribute("itemDTO") ItemDTO dto, BindingResult binding, RedirectAttributes redirectAttributes) {
        if (binding.hasErrors()) {
            return rejectForm(binding, redirectAttributes, "redirect:/staff/inventory/add-non-perishable");
        }
        try {
            inventoryService.saveNonPerishableItem(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Non-Perishable item added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + StaffMessages.friendly(e, "please check the form and try again."));
        }
        return LIST;
    }

    private String rejectForm(BindingResult binding, RedirectAttributes redirectAttributes, String backTo) {
        String message = binding.getFieldErrors().stream()
                .map(e -> e.getDefaultMessage() != null && !e.getDefaultMessage().startsWith("Failed to convert")
                        ? e.getDefaultMessage()
                        : "Please check the \"" + e.getField() + "\" field.")
                .findFirst()
                .orElse("please check the form and try again.");
        redirectAttributes.addFlashAttribute("errorMessage", "Error adding item: " + message);
        return backTo;
    }

    // ---------------------------------------------------------------- categories

    @GetMapping("/categories")
    public String listCategories(Model model) {
        model.addAttribute("categories", inventoryService.getCategorySummaries());
        return "inventory/categories";
    }

    @AdminOnly
    @PostMapping("/categories")
    public String createCategory(@RequestParam String name, RedirectAttributes redirectAttributes) {
        try {
            ItemCategoryDTO created = inventoryService.createCategory(name);
            redirectAttributes.addFlashAttribute("successMessage", "Category \"" + created.getName() + "\" created.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not create the category: " + StaffMessages.friendly(e, "please try again."));
        }
        return CATEGORIES;
    }

    @AdminOnly
    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            String name = inventoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category \"" + name + "\" deleted.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not delete the category: " + StaffMessages.friendly(e, "please try again."));
        }
        return CATEGORIES;
    }

    @AdminOnly
    @GetMapping("/items/{id}/categories")
    public String showEditItemCategories(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("item", inventoryService.getItemForEdit(id));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not open that item."));
            return LIST;
        }
        model.addAttribute("categories", inventoryService.getAllCategories());
        return "inventory/edit-item-categories";
    }

    @AdminOnly
    @PostMapping("/items/{id}/categories")
    public String saveItemCategories(@PathVariable Long id, @RequestParam(required = false) List<Long> categoryIds, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.updateItemCategories(id, categoryIds);
            redirectAttributes.addFlashAttribute("successMessage", "Categories updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update the categories: " + StaffMessages.friendly(e, "please try again."));
        }
        return LIST;
    }

    @AdminOnly
    @GetMapping("/items/{id}/image")
    public String showEditItemImage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("item", inventoryService.getItemForEdit(id));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not open that item."));
            return LIST;
        }
        return "inventory/edit-item-image";
    }

    @AdminOnly
    @PostMapping("/items/{id}/image")
    public String saveItemImage(@PathVariable Long id, @RequestParam("imageFile") MultipartFile imageFile, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.replaceItemImage(id, imageFile);
            redirectAttributes.addFlashAttribute("successMessage", "Picture updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update the picture: " + StaffMessages.friendly(e, "please try again."));
            return "redirect:/staff/inventory/items/" + id + "/image";
        }
        return LIST;
    }

    @AdminOnly
    @PostMapping("/items/{id}/image/remove")
    public String removeItemImage(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.removeItemImage(id);
            redirectAttributes.addFlashAttribute("successMessage", "Picture removed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not remove the picture: " + StaffMessages.friendly(e, "please try again."));
        }
        return LIST;
    }

    @PostMapping("/quick-update")
    public String quickUpdateStock(@ModelAttribute InventoryUpdateDTO dto, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            // Whatever the form sent, the log entry is written under the logged-in employee.
            dto.setEmployeeId(StaffSession.require(session).getId());
            inventoryService.performQuickStockUpdate(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Stock updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Update failed: " + StaffMessages.friendly(e, "please try again."));
        }
        return LIST;
    }

    @AdminOnly
    @PostMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            inventoryService.deleteItem(id);
            redirectAttributes.addFlashAttribute("successMessage", "Item deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete item: " + StaffMessages.friendly(e, "it may be in a cart or an order."));
        }
        return LIST;
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
