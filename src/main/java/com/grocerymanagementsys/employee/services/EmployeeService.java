package com.grocerymanagementsys.employee.services;

import com.grocerymanagementsys.common.dtos.PasswordChangeDto;
import com.grocerymanagementsys.common.exceptions.EmailAlreadyUsedException;
import com.grocerymanagementsys.common.exceptions.InvalidCredentialsException;
import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.employee.dtos.EmployeeRequestDto;
import com.grocerymanagementsys.employee.dtos.EmployeeResponseDto;
import com.grocerymanagementsys.employee.dtos.EmployeeUpdateDto;
import com.grocerymanagementsys.employee.entities.Employee;
import com.grocerymanagementsys.employee.repositories.EmployeeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final Validator validator;

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

    public List<EmployeeResponseDto> findAll() {
        return employeeRepository.findAll(Sort.by("role", "fullName")).stream().map(this::toResponse).toList();
    }

    @Transactional
    public EmployeeResponseDto updateProfile(Integer id, EmployeeUpdateDto dto) {
        check(dto);
        Employee employee = findOrThrow(id);
        String email = dto.email().trim();
        if (employeeRepository.findByEmail(email).filter(other -> !other.getId().equals(id)).isPresent()) {
            throw new EmailAlreadyUsedException(email);
        }
        employee.setFullName(dto.fullName().trim());
        employee.setEmail(email);
        return toResponse(employeeRepository.save(employee));
    }

    @Transactional
    public void changePassword(Integer id, PasswordChangeDto dto) {
        check(dto);
        Employee employee = findOrThrow(id);
        if (!passwordEncoder.matches(dto.currentPassword(), employee.getPasswordHash())) {
            throw new ValidationException("Current password is incorrect.");
        }
        employee.setPasswordHash(passwordEncoder.encode(dto.newPassword()));
        employeeRepository.save(employee);
    }

    /** Removes a staff or admin account. An account can never delete itself. */
    @Transactional
    public String delete(Integer id, Integer actingEmployeeId) {
        if (id.equals(actingEmployeeId)) {
            throw new ValidationException("You can't delete your own account.");
        }
        Employee employee = findOrThrow(id);
        employeeRepository.delete(employee);
        employeeRepository.flush(); // surface a foreign-key problem here, not at commit time
        return employee.getFullName();
    }

    private Employee findOrThrow(Integer id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee", id));
    }

    private void check(Object dto) {
        String problems = validator.validate(dto).stream()
                .map((ConstraintViolation<Object> v) -> v.getPropertyPath() + " " + v.getMessage())
                .sorted()
                .collect(Collectors.joining("; "));
        if (!problems.isEmpty()) {
            throw new ValidationException(problems);
        }
    }

    private EmployeeResponseDto toResponse(Employee e) {
        return new EmployeeResponseDto(e.getId(), e.getFullName(), e.getEmail(), e.getRole());
    }
}
