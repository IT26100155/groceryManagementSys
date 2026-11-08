package com.grocerymanagementsys.employee.dtos;

import com.grocerymanagementsys.employee.enums.EmployeeRole;

public record EmployeeResponseDto(
        Integer id,
        String fullName,
        String email,
        EmployeeRole role
) { }
