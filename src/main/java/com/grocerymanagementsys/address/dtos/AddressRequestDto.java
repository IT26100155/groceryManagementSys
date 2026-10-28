package com.grocerymanagementsys.address.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressRequestDto(
        @NotBlank @Size(max = 500) String addressBody
) { }
