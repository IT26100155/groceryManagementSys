package com.grocerymanagementsys.discount.services;


import com.grocerymanagementsys.common.exceptions.ResourceNotFoundException;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos.DiscountRequestDto;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.dtos.DiscountResponseDto;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.Discount;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.entities.DiscountedItem;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories.DiscountRepository;
import com.grocerymanagementsys.src.main.java.com.grocerymanagementsys.discount.repositories.DiscountedItemRepository;
import com.grocerymanagementsys.inventory.repositories.ItemRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiscountService {
    private final DiscountRepository discountRepository;
    private final DiscountedItemRepository discountedItemRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public DiscountResponseDto create(DiscountRequestDto dto) {
        if (!dto.startDate().isBefore(dto.expireDate())) {
            throw new ValidationException("startDate must be before expireDate");
        }
        boolean hasItems = dto.itemIds() != null && !dto.itemIds().isEmpty();
        if (!dto.storeWide() && !hasItems) {
            throw new ValidationException("itemIds is required when the discount is not store-wide");
        }
        if (hasItems) {
            validateItemsExist(dto.itemIds());
        }

        Discount discount = new Discount();
        discount.setPercentage(dto.percentage());
        discount.setMinPrice(dto.minPrice());
        discount.setStartDate(dto.startDate());
        discount.setExpireDate(dto.expireDate());
        discount.setEmployeeId(dto.employeeId());
        discount.setStoreWide(dto.storeWide());
        discount.setActive(true);
        discount = discountRepository.save(discount);

        // A store-wide discount applies to everything, so it never gets item rows.
        if (!dto.storeWide()) {
            saveItems(discount.getId(), dto.itemIds());
        }
        return toResponse(discount);
    }

    // Replaces the set of items a discount applies to.
    @Transactional
    public DiscountResponseDto updateItems(Integer discountId, List<Integer> itemIds) {
        Discount discount = findDiscountOrThrow(discountId);
        if (discount.isStoreWide()) {
            throw new ValidationException("A store-wide discount applies to every item and has no item list");
        }
        validateItemsExist(itemIds);

        discountedItemRepository.deleteByDiscountId(discountId);
        discountedItemRepository.flush(); // delete must reach the DB before re-inserting, or the unique constraint can trip
        saveItems(discountId, itemIds);
        return toResponse(discount);
    }

    public List<DiscountResponseDto> getActive() {
        return discountRepository.findByActiveTrue().stream()
                .map(this::toResponse)
                .toList();
    }

    public DiscountResponseDto deactivate(Integer id) {
        Discount discount = findDiscountOrThrow(id);
        discount.setActive(false);
        return toResponse(discountRepository.save(discount));
    }

    private Discount findDiscountOrThrow(Integer id) {
        return discountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Discount", id));
    }

    private void validateItemsExist(List<Integer> itemIds) {
        for (Integer itemId : itemIds) {
            if (!itemRepository.existsById(itemId)) {
                throw new ResourceNotFoundException("Item", itemId);
            }
        }
    }

    private void saveItems(Integer discountId, List<Integer> itemIds) {
        for (Integer itemId : itemIds.stream().distinct().toList()) {
            DiscountedItem link = new DiscountedItem();
            link.setDiscountId(discountId);
            link.setItemId(itemId);
            discountedItemRepository.save(link);
        }
    }

    private DiscountResponseDto toResponse(Discount d) {
        List<Integer> itemIds = discountedItemRepository.findByDiscountId(d.getId()).stream()
                .map(DiscountedItem::getItemId)
                .toList();

        return new DiscountResponseDto(
                d.getId(), d.getPercentage(), d.getMinPrice(), d.getStartDate(),
                d.getExpireDate(), d.isActive(), d.getEmployeeId(), d.isStoreWide(), itemIds
        );
    }
}
