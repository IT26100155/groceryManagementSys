package com.grocerymanagementsys.customer.dtos;

public record CustomerResponseDto(
        Integer id,
        String fullName,
        String email,
        String phoneNumber,
        Integer preference
) { }
