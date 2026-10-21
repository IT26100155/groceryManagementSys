package com.grocerymanagementsys.customer.services;

import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.InvalidCredentialsException;
import com.grocerymanagementsys.customer.dtos.CustomerRequestDto;
import com.grocerymanagementsys.customer.dtos.CustomerResponseDto;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.customer.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerResponseDto signUp(CustomerRequestDto dto) {
        if (customerRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyUsedException(dto.email());
        }

        Customer customer = new Customer();
        customer.setEmail(dto.email());
        customer.setPasswordHash( passwordEncoder.encode(dto.password()));
        customer.setFullName(dto.fullName());
        customer.setPhoneNumber(dto.phoneNumber());

        return toCustomerResponse(
                customerRepository.save(customer)
        );
    }

    public CustomerResponseDto login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, customer.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return toCustomerResponse(customer);
    }

    private CustomerResponseDto toCustomerResponse(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getPreference()
        );
    }

}
