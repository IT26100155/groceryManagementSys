package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.order.dtos.StaffOrderDto;
import com.grocerymanagementsys.order.enums.OrderStatus;
import com.grocerymanagementsys.order.services.OrderService;
import com.grocerymanagementsys.payment.enums.PaymentState;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/** Orders with a verified payment. Any staff member can move them between Confirmed, Shipped and Delivered. */
@Controller
@RequestMapping("/staff/orders")
@RequiredArgsConstructor
public class StaffOrderController {
    private final OrderService orderService;

    @GetMapping
    public String list(@RequestParam(required = false) OrderStatus status, Model model) {
        List<StaffOrderDto> orders = orderService.getStaffOrdersByPaymentState(PaymentState.CONFIRMED);
        if (status != null) {
            orders = orders.stream().filter(o -> o.status() == status).toList();
        }
        model.addAttribute("orders", orders);
        model.addAttribute("filter", status);
        model.addAttribute("filters", List.of(OrderStatus.CONFIRMED, OrderStatus.SHIPPED, OrderStatus.DELIVERED));
        model.addAttribute("editableStatuses", List.of(OrderStatus.CONFIRMED, OrderStatus.SHIPPED, OrderStatus.DELIVERED));
        return "staff/orders";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Integer id, @RequestParam OrderStatus status,
                               @RequestParam(required = false) OrderStatus filter, RedirectAttributes redirect) {
        try {
            orderService.updateStatus(id, status);
            redirect.addFlashAttribute("successMessage", "Order #" + id + " is now " + status + ".");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Could not update order #" + id + ": " + StaffMessages.friendly(e, "please try again."));
        }
        return filter == null ? "redirect:/staff/orders" : "redirect:/staff/orders?status=" + filter;
    }
}
