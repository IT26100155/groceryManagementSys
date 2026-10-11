package com.grocerymanagementsys.discount.controller;

import com.grocerymanagementsys.discount.dtos.DiscountRequestDto;
import com.grocerymanagementsys.discount.dtos.DiscountResponseDto;
import com.grocerymanagementsys.discount.dtos.UpdateDiscountItemsDto;
import com.grocerymanagementsys.discount.services.DiscountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/discounts")
@RequiredArgsConstructor
public class DiscountController {
    private final DiscountService discountService;

    @PostMapping
    public ResponseEntity<DiscountResponseDto> create(@RequestBody @Valid DiscountRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.create(request));
    }

    @GetMapping
    public ResponseEntity<List<DiscountResponseDto>> getActive() {
        return ResponseEntity.ok(discountService.getActive());
    }

    @PutMapping("/{id}/items")
    public ResponseEntity<DiscountResponseDto> updateItems(@PathVariable Integer id, @RequestBody @Valid UpdateDiscountItemsDto request) {
        return ResponseEntity.ok(discountService.updateItems(id, request.itemIds()));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<DiscountResponseDto> deactivate(@PathVariable Integer id) {
        return ResponseEntity.ok(discountService.deactivate(id));
    }
}
