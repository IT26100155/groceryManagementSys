package com.grocerymanagementsys.inventory;

import com.grocerymanagementsys.inventory.entities.ItemCategory;
import com.grocerymanagementsys.inventory.repositories.ItemCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ItemCategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        if (categoryRepository.count() == 0) {
            List<String> categories = List.of(
                    "Dairy & Eggs",
                    "Grains & Bakery",
                    "Fresh Produce",
                    "Pantry & Staples",
                    "Beverages",
                    "Frozen Foods"
            );

            for (String catName : categories) {
                categoryRepository.save(ItemCategory.builder().name(catName).build());
            }
        }
    }
}
