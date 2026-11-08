package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.common.dtos.PasswordChangeDto;
import com.grocerymanagementsys.employee.dtos.EmployeeResponseDto;
import com.grocerymanagementsys.employee.dtos.EmployeeUpdateDto;
import com.grocerymanagementsys.employee.services.EmployeeService;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import com.grocerymanagementsys.staff.utility.StaffSession;
import com.grocerymanagementsys.staff.utility.StaffUser;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Lets any logged-in staff member or admin edit their own account details. */
@Controller
@RequestMapping("/staff/account")
@RequiredArgsConstructor
public class StaffAccountController {
    private static final String ACCOUNT = "redirect:/staff/account";
    private final EmployeeService employeeService;

    @GetMapping
    public String view() {
        return "staff/account";
    }

    @PostMapping
    public String updateDetails(@RequestParam String fullName, @RequestParam String email,
                                HttpSession session, RedirectAttributes redirectAttributes) {
        StaffUser me = StaffSession.require(session);
        try {
            EmployeeResponseDto saved = employeeService.updateProfile(me.getId(), new EmployeeUpdateDto(fullName, email));
            session.setAttribute(StaffSession.USER, new StaffUser(saved.id(), saved.fullName(), saved.email(), saved.role()));
            redirectAttributes.addFlashAttribute("successMessage", "Your details were updated.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not update your details: " + StaffMessages.friendly(e, "please try again."));
        }
        return ACCOUNT;
    }

    @PostMapping("/password")
    public String changePassword(@RequestParam String currentPassword, @RequestParam String newPassword,
                                 @RequestParam String confirmPassword, HttpSession session, RedirectAttributes redirectAttributes) {
        StaffUser me = StaffSession.require(session);
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "The new password and its confirmation don't match.");
            return ACCOUNT;
        }
        try {
            employeeService.changePassword(me.getId(), new PasswordChangeDto(currentPassword, newPassword));
            redirectAttributes.addFlashAttribute("successMessage", "Your password was changed.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Could not change your password: " + StaffMessages.friendly(e, "please try again."));
        }
        return ACCOUNT;
    }
}
