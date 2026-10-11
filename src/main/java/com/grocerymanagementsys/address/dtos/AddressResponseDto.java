package com.grocerymanagementsys.address.dtos;

public record AddressResponseDto(
        Integer id,
        Integer customerId,
        String addressBody
) { }
