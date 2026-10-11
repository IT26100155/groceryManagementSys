package com.grocerymanagementsys.employee.dtos;

import com.grocerymanagementsys.employee.enums.EmployeeRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record EmployeeRequestDto(
        @NotBlank @Size(max = 200) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 100) String password,
        @NotNull EmployeeRole role
) { }
