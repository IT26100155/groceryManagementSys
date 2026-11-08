package com.grocerymanagementsys.staff.controller;

import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.InvalidCredentialsException;
import com.grocerymanagementsys.employee.dtos.EmployeeRequestDto;
import com.grocerymanagementsys.employee.enums.EmployeeRole;
import com.grocerymanagementsys.employee.dtos.EmployeeResponseDto;
import com.grocerymanagementsys.employee.services.EmployeeService;
import com.grocerymanagementsys.staff.utility.StaffSession;
import com.grocerymanagementsys.staff.utility.StaffUser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffAuthController {
    private static final String HOME = "redirect:/staff/inventory/items";
    private final EmployeeService employeeService;
    private final Validator validator;

    @GetMapping({"", "/"})
    public String home() {
        return HOME;
    }

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        return StaffSession.current(session) != null ? HOME : "staff/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
                        HttpServletRequest request, Model model) {
        try {
            EmployeeResponseDto employee = employeeService.login(email.trim(), password);
            request.changeSessionId();
            request.getSession().setAttribute(StaffSession.USER,
                    new StaffUser(employee.id(), employee.fullName(), employee.email(), employee.role()));
            return HOME;
        } catch (InvalidCredentialsException e) {
            model.addAttribute("error", "Invalid email or password.");
            model.addAttribute("email", email);
            return "staff/login";
        }
    }

    // Hidden sign-up page: not linked from anywhere, you reach it by typing /staff/signup.
    @GetMapping("/signup")
    public String signupPage(HttpSession session) {
        return StaffSession.current(session) != null ? HOME : "staff/signup";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String fullName, @RequestParam String email,
                         @RequestParam String password, @RequestParam String confirmPassword,
                         @RequestParam EmployeeRole role, Model model, RedirectAttributes redirect) {
        model.addAttribute("fullName", fullName);
        model.addAttribute("email", email);
        model.addAttribute("role", role.name());
        try {
            if (!password.equals(confirmPassword)) {
                throw new IllegalArgumentException("The two passwords don't match.");
            }
            EmployeeRequestDto dto = new EmployeeRequestDto(fullName.trim(), email.trim(), password, role);
            Set<ConstraintViolation<EmployeeRequestDto>> problems = validator.validate(dto);
            if (!problems.isEmpty()) {
                throw new IllegalArgumentException(problems.stream()
                        .map(v -> v.getPropertyPath() + " " + v.getMessage())
                        .sorted()
                        .collect(Collectors.joining("; ")));
            }
            employeeService.signUp(dto);
            redirect.addFlashAttribute("successMessage", "Account created. You can log in now.");
            return "redirect:/staff/login";
        } catch (IllegalArgumentException | EmailAlreadyUsedException e) {
            model.addAttribute("error", e.getMessage());
            return "staff/signup";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/staff/login";
    }
}
