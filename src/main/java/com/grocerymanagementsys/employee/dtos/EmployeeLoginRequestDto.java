package com.grocerymanagementsys.employee.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record EmployeeLoginRequestDto(
        @NotBlank @Email String email,
        @NotBlank String password
) { }
