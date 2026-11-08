package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.payment.services.PaymentService;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/staff/payments")
@RequiredArgsConstructor
public class StaffPaymentController {
    private static final String PAGE = "redirect:/staff/payments";
    private final PaymentService paymentService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("pending", paymentService.getPending());
        return "staff/payments";
    }

    @PostMapping("/{id}/confirm")
    public String confirm(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            paymentService.confirm(id);
            redirect.addFlashAttribute("successMessage", "Payment confirmed.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not confirm the payment."));
        }
        return PAGE;
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Integer id, RedirectAttributes redirect) {
        try {
            paymentService.cancel(id);
            redirect.addFlashAttribute("successMessage", "Payment cancelled.");
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", StaffMessages.friendly(e, "Could not cancel the payment."));
        }
        return PAGE;
    }
}
