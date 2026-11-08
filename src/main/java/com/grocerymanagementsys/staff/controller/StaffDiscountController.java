package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.discount.dtos.DiscountRequestDto;
import com.grocerymanagementsys.discount.dtos.DiscountResponseDto;
import com.grocerymanagementsys.discount.services.DiscountService;
import com.grocerymanagementsys.inventory.dtos.ItemDTO;
import com.grocerymanagementsys.inventory.services.InventoryService;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import com.grocerymanagementsys.staff.interfaces.AdminOnly;
import com.grocerymanagementsys.staff.utility.StaffSession;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff/discounts")
@RequiredArgsConstructor
public class StaffDiscountController {
    private static final String PAGE = "redirect:/staff/discounts";

    private final DiscountService discountService;
    private final InventoryService inventoryService;
    private final Validator validator;

    public record DiscountRow(DiscountResponseDto discount, String scope, boolean upcoming) { }

    @GetMapping
    public String list(Model model) {
        LocalDate today = LocalDate.now();
        List<ItemDTO> items = inventoryService.getAllItems(null, null);
        Map<Long, String> itemNames = items.stream().collect(Collectors.toMap(ItemDTO::getId, ItemDTO::getName));

        List<DiscountRow> rows = discountService.getActive().stream()
                .map(d -> new DiscountRow(
                        d,
                        d.storeWide() ? "Whole store" : d.itemIds().stream()
                                .map(id -> itemNames.getOrDefault(id, "Item " + id))
                                .collect(Collectors.joining(", ")),
                        d.startDate().isAfter(today)))
                .toList();

        model.addAttribute("rows", rows);
        model.addAttribute("items", items);
        return "staff/discounts";
    }

    @AdminOnly
    @PostMapping
    public String create(
            @RequestParam Integer percentage,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expireDate,
            @RequestParam(defaultValue = "false") boolean storeWide,
            @RequestParam(required = false) List<Long> itemIds,
            HttpSession session, RedirectAttributes redirect
    ) {

        try {
            DiscountRequestDto dto = new DiscountRequestDto(percentage, minPrice, startDate, expireDate, StaffSession.require(session).getId(), storeWide, itemIds);

            Set<ConstraintViolation<DiscountRequestDto>> problems = validator.validate(dto);
            if (!problems.isEmpty()) {
                throw new ValidationException(problems.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .sorted()
                        .collect(Collectors.joining("; ")));
            }

            discountService.create(dto);
            redirect.addFlashAttribute("successMessage", "Discount created.");

        }

        catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not create the discount."));
        }
        return PAGE;
    }

    @AdminOnly
    @PostMapping("/{id}/items")
    public String updateItems(@PathVariable Integer id, @RequestParam(required = false) List<Long> itemIds, RedirectAttributes redirect) {
        try {
            if (itemIds == null || itemIds.isEmpty()) {
                throw new ValidationException("Pick at least one item.");
            }
            discountService.updateItems(id, itemIds);
            redirect.addFlashAttribute("successMessage", "Discount items saved.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not save the items."));
        }
        return PAGE;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            discountService.deactivate(id);
            redirect.addFlashAttribute("successMessage", "Discount deactivated.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not deactivate the discount."));
        }
        return PAGE;
    }
}
