package com.grocerymanagementsys.employee.controller;

import com.grocerymanagementsys.employee.dtos.EmployeeLoginRequestDto;
import com.grocerymanagementsys.employee.dtos.EmployeeRequestDto;
import com.grocerymanagementsys.employee.dtos.EmployeeResponseDto;
import com.grocerymanagementsys.employee.services.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @PostMapping("/signup")
    public ResponseEntity<EmployeeResponseDto> signUp(@RequestBody @Valid EmployeeRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<EmployeeResponseDto> login(@RequestBody @Valid EmployeeLoginRequestDto request) {
        return ResponseEntity.ok(employeeService.login(request.email(), request.password()));
    }
}
