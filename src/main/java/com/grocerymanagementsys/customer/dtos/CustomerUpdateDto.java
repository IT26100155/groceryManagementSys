package com.grocerymanagementsys.customer.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CustomerUpdateDto(
        @NotBlank @Size(max = 200) String fullName,
        @NotBlank @Email String email,
        @NotBlank @Size(max = 20) String phoneNumber
) { }
