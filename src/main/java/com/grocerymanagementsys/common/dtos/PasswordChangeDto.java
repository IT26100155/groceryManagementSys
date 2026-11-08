package com.grocerymanagementsys.common.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordChangeDto(
        @NotBlank String currentPassword,
        @NotBlank @Size(min = 8, max = 100) String newPassword
) { }
