package com.grocerymanagementsys.employee.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmployeeUpdateDto(
        @NotBlank @Size(max = 200) String fullName,
        @NotBlank @Email String email
) { }
