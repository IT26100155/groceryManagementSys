package com.grocerymanagementsys.payment.controller;

import com.grocerymanagementsys.payment.dtos.InitiatePaymentDto;
import com.grocerymanagementsys.payment.dtos.PaymentResponseDto;
import com.grocerymanagementsys.payment.services.PaymentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/orders/{orderId}")
    public ResponseEntity<PaymentResponseDto> initiate(@PathVariable Integer orderId, @RequestBody @Valid InitiatePaymentDto request) {
        PaymentResponseDto payment = paymentService.initiate(orderId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<List<PaymentResponseDto>> getByOrder(@PathVariable Integer orderId) {
        return ResponseEntity.ok(paymentService.getByOrder(orderId));
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<PaymentResponseDto> confirm(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.confirm(id));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<PaymentResponseDto> cancel(@PathVariable Integer id) {
        return ResponseEntity.ok(paymentService.cancel(id));
    }

}