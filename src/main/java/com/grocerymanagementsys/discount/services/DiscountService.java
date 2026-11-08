package com.grocerymanagementsys.discount.services;

import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.discount.dtos.DiscountRequestDto;
import com.grocerymanagementsys.discount.dtos.DiscountResponseDto;
import com.grocerymanagementsys.discount.entities.Discount;
import com.grocerymanagementsys.discount.repositories.DiscountRepository;
import com.grocerymanagementsys.employee.entities.Employee;
import com.grocerymanagementsys.employee.repositories.EmployeeRepository;
import com.grocerymanagementsys.inventory.entities.Item;
import com.grocerymanagementsys.inventory.repositories.ItemRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final ItemRepository itemRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public DiscountResponseDto create(DiscountRequestDto dto) {
        if (dto.startDate().isAfter(dto.expireDate())) {
            throw new ValidationException("startDate cannot be after expireDate");
        }
        boolean hasItems = dto.itemIds() != null && !dto.itemIds().isEmpty();
        if (!dto.storeWide() && !hasItems) {
            throw new ValidationException("itemIds is required when the discount is not store-wide");
        }

        Employee employee = employeeRepository.findById(dto.employeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", dto.employeeId()));

        Discount discount = new Discount();
        discount.setPercentage(dto.percentage());
        discount.setMinPrice(dto.minPrice());
        discount.setStartDate(dto.startDate());
        discount.setExpireDate(dto.expireDate());
        discount.setEmployee(employee);
        discount.setStoreWide(dto.storeWide());
        discount.setActive(true);

        if (!dto.storeWide()) {
            discount.setItems(loadItems(dto.itemIds()));
        }
        return toResponse(discountRepository.save(discount));
    }

    @Transactional
    public DiscountResponseDto updateItems(Integer discountId, List<Long> itemIds) {
        Discount discount = findDiscountOrThrow(discountId);
        if (discount.isStoreWide()) {
            throw new ValidationException("A store-wide discount applies to every item and has no item list");
        }

        Set<Item> items = loadItems(itemIds);
        discount.getItems().clear();
        discount.getItems().addAll(items);
        return toResponse(discount);
    }

    @Transactional(readOnly = true)
    public List<DiscountResponseDto> getActive() {
        LocalDate today = LocalDate.now();
        return discountRepository.findByActiveTrue().stream()
                .filter(d -> !d.getExpireDate().isBefore(today))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public DiscountResponseDto deactivate(Integer id) {
        Discount discount = findDiscountOrThrow(id);
        discount.setActive(false);
        return toResponse(discountRepository.save(discount));
    }

    private Discount findDiscountOrThrow(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
    }

    private Set<Item> loadItems(List<Long> itemIds) {
        Set<Long> wanted = new HashSet<>(itemIds);
        List<Item> found = itemRepository.findAllById(wanted);
        if (found.size() != wanted.size()) {
            Set<Long> foundIds = found.stream().map(Item::getId).collect(Collectors.toSet());
            Long missing = wanted.stream().filter(id -> !foundIds.contains(id)).findFirst().orElseThrow();
            throw new ResourceNotFoundException("Item", missing);
        }
        return new HashSet<>(found);
    }

    private DiscountResponseDto toResponse(Discount d) {
        List<Long> itemIds = d.getItems().stream()
                .map(Item::getId)
                .sorted()
                .toList();

        return new DiscountResponseDto(
                d.getId(),
                d.getPercentage(),
                d.getMinPrice(),
                d.getStartDate(),
                d.getExpireDate(),
                d.isActive(),
                d.getEmployee().getId(),
                d.isStoreWide(),
                itemIds
        );
    }
}
