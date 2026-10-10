package com.grocerymanagementsys.order.controller;

import com.grocerymanagementsys.order.dtos.OrderResponseDto;
import com.grocerymanagementsys.order.dtos.PlaceOrderDto;
import com.grocerymanagementsys.order.services.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponseDto> placeOrder(@RequestBody @Valid PlaceOrderDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.placeOrder(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponseDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(orderService.getById(id));
    }

    @GetMapping("/customers/{customerId}")
    public ResponseEntity<List<OrderResponseDto>> getByCustomer(@PathVariable Integer customerId) {
        return ResponseEntity.ok(orderService.getByCustomer(customerId));
    }
}
