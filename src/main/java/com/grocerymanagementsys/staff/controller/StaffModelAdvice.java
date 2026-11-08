package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.inventory.controller.InventoryController;
import com.grocerymanagementsys.staff.utility.StaffSession;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice(assignableTypes = {StaffAuthController.class, StaffDiscountController.class, StaffPaymentController.class, InventoryController.class, StaffTeamController.class, StaffAccountController.class, StaffOrderController.class})
public class StaffModelAdvice {

    @ModelAttribute
    public void addStaffAttributes(HttpSession session, Model model) {
        model.addAttribute("csrfToken", StaffSession.csrfToken(session));
        model.addAttribute("staffUser", StaffSession.current(session));
    }
}
