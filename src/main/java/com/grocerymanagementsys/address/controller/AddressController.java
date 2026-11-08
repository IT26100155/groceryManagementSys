package com.grocerymanagementsys.address.controller;

import com.grocerymanagementsys.address.dtos.AddressRequestDto;
import com.grocerymanagementsys.address.dtos.AddressResponseDto;
import com.grocerymanagementsys.address.services.AddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers/{customerId}/addresses")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @PostMapping
    public ResponseEntity<AddressResponseDto> create(@PathVariable Integer customerId, @RequestBody @Valid AddressRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(customerId, request));
    }

    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getAll(@PathVariable Integer customerId) {
        return ResponseEntity.ok(addressService.getAll(customerId));
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> update(@PathVariable Integer customerId, @PathVariable Integer addressId, @RequestBody @Valid AddressRequestDto request) {
        return ResponseEntity.ok(addressService.update(customerId, addressId, request));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Integer customerId, @PathVariable Integer addressId) {
        addressService.delete(customerId, addressId);
        return ResponseEntity.noContent().build();
    }
}
