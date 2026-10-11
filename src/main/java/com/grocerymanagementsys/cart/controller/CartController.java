package com.grocerymanagementsys.cart.controller;

import com.grocerymanagementsys.cart.dtos.AddItemDto;
import com.grocerymanagementsys.cart.dtos.CartResponseDto;
import com.grocerymanagementsys.cart.services.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers/{customerId}/cart")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@PathVariable Integer customerId) {
        return ResponseEntity.ok(cartService.getCart(customerId));
    }

    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(@PathVariable Integer customerId, @RequestBody @Valid AddItemDto request) {
        return ResponseEntity.ok(cartService.addItem(customerId, request));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartResponseDto> removeItem(@PathVariable Integer customerId, @PathVariable Integer itemId) {
        return ResponseEntity.ok(cartService.removeItem(customerId, itemId));
    }
}
