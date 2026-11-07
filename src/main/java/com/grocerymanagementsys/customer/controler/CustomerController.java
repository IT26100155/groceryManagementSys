package com.grocerymanagementsys.customer.controler;

import com.grocerymanagementsys.common.dtos.PasswordChangeDto;
import com.grocerymanagementsys.customer.dtos.CustomerRequestDto;
import com.grocerymanagementsys.customer.dtos.CustomerResponseDto;
import com.grocerymanagementsys.customer.dtos.CustomerUpdateDto;
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

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDto> updateProfile(@PathVariable Integer id, @RequestBody @Valid CustomerUpdateDto request) {
        return ResponseEntity.ok(customerService.updateProfile(id, request));
    }

    @PutMapping("/{id}/password")
    public ResponseEntity<Void> changePassword(@PathVariable Integer id, @RequestBody @Valid PasswordChangeDto request) {
        customerService.changePassword(id, request);
        return ResponseEntity.noContent().build();
    }
}
