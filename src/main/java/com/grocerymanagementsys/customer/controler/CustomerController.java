package com.grocerymanagementsys.customer.controler;

import com.grocerymanagementsys.customer.dtos.CustomerRequestDto;
import com.grocerymanagementsys.customer.dtos.CustomerResponseDto;
import com.grocerymanagementsys.customer.dtos.LoginRequestDto;
import com.grocerymanagementsys.customer.services.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    
    @PostMapping("/signup")
    public ResponseEntity<CustomerResponseDto> signUp(@RequestBody @Valid CustomerRequestDto request) {
        CustomerResponseDto responseCreated = customerService.signUp(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreated);
    }
    
    @PostMapping("/login")
    public ResponseEntity<CustomerResponseDto> login(@RequestBody @Valid LoginRequestDto request) {
        CustomerResponseDto customer = customerService.login(request.email(), request.password());
        return ResponseEntity.ok(customer);
    }
}
