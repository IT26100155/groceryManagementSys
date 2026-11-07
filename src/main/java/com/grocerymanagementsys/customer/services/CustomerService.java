package com.grocerymanagementsys.customer.services;

import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.InvalidCredentialsException;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.customer.dtos.CustomerRequestDto;
import com.grocerymanagementsys.common.dtos.PasswordChangeDto;
import com.grocerymanagementsys.customer.dtos.CustomerResponseDto;
import com.grocerymanagementsys.customer.dtos.CustomerUpdateDto;
import com.grocerymanagementsys.customer.entities.Customer;
import com.grocerymanagementsys.customer.repositories.CustomerRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        customer.setPasswordHash(passwordEncoder.encode(dto.password()));
        customer.setFullName(dto.fullName());
        customer.setPhoneNumber(dto.phoneNumber());

        return toCustomerResponse(customerRepository.save(customer));
    }

    public CustomerResponseDto login(String email, String rawPassword) {
        Customer customer = customerRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, customer.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return toCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponseDto updateProfile(Integer id, CustomerUpdateDto dto) {
        Customer customer = findCustomerOrThrow(id);
        String email = dto.email().trim();
        if (customerRepository.findByEmail(email).filter(other -> !other.getId().equals(id)).isPresent()) {
            throw new EmailAlreadyUsedException(email);
        }
        customer.setFullName(dto.fullName().trim());
        customer.setEmail(email);
        customer.setPhoneNumber(dto.phoneNumber().trim());
        return toCustomerResponse(customerRepository.save(customer));
    }

    @Transactional
    public void changePassword(Integer id, PasswordChangeDto dto) {
        Customer customer = findCustomerOrThrow(id);
        if (!passwordEncoder.matches(dto.currentPassword(), customer.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        customer.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        customerRepository.save(customer);
    }

    public Customer findCustomerOrThrow(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", id));
    }

    private CustomerResponseDto toCustomerResponse(Customer customer) {
        return new CustomerResponseDto(
                customer.getId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }
}
