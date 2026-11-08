package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.employee.services.EmployeeService;
import com.grocerymanagementsys.staff.interfaces.AdminOnly;
import com.grocerymanagementsys.staff.utility.StaffMessages;
import com.grocerymanagementsys.staff.utility.StaffSession;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/** Admin-only view of all staff and admin accounts, with the option to delete them. */
@Controller
@RequestMapping("/staff/team")
@AdminOnly
@RequiredArgsConstructor
public class StaffTeamController {
    private static final String TEAM = "redirect:/staff/team";
    private final EmployeeService employeeService;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "staff/team";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            String name = employeeService.delete(id, StaffSession.require(session).getId());
            redirectAttributes.addFlashAttribute("successMessage", "Account for " + name + " deleted.");
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "This account can't be deleted because it is linked to stock updates or discounts it created.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not delete the account: " + StaffMessages.friendly(e, "please try again."));
        }
        return TEAM;
    }
}
