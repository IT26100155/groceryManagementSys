package com.grocerymanagementsys.employee.services;

import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.InvalidCredentialsException;
import com.grocerymanagementsys.employee.dtos.EmployeeRequestDto;
import com.grocerymanagementsys.employee.dtos.EmployeeResponseDto;
import com.grocerymanagementsys.employee.entities.Employee;
import com.grocerymanagementsys.employee.repositories.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public EmployeeResponseDto signUp(EmployeeRequestDto dto) {
        if (employeeRepository.existsByEmail(dto.email())) {
            throw new EmailAlreadyUsedException(dto.email());
        }

        Employee employee = new Employee();
        employee.setFullName(dto.fullName());
        employee.setEmail(dto.email());
        employee.setPasswordHash(passwordEncoder.encode(dto.password()));
        employee.setRole(dto.role());

        return toResponse(employeeRepository.save(employee));
    }

    public EmployeeResponseDto login(String email, String rawPassword) {
        Employee employee = employeeRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, employee.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return toResponse(employee);
    }

    private EmployeeResponseDto toResponse(Employee e) {
        return new EmployeeResponseDto(e.getId(), e.getFullName(), e.getEmail(), e.getRole());
    }
}
