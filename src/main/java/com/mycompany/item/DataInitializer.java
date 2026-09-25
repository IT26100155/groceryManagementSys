package com.mycompany.item;

import com.mycompany.item.model.*;
import com.mycompany.item.repository.*;
import com.mycompany.item.service.ItemService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemService itemService;

    public DataInitializer(CategoryRepository categoryRepository,
                           ItemRepository itemRepository,
                           ItemImageRepository itemImageRepository,
                           ItemService itemService) {
        this.categoryRepository = categoryRepository;
        this.itemRepository = itemRepository;
        this.itemImageRepository = itemImageRepository;
        this.itemService = itemService;
    }

    @Override
    public void run(String... args) {
        // 1. Categories
        Category produce = categoryRepository.save(new Category("Produce"));
        Category dairy = categoryRepository.save(new Category("Dairy & Eggs"));
        Category bakery = categoryRepository.save(new Category("Bakery"));
        Category pantry = categoryRepository.save(new Category("Pantry"));

        // 2. Items & Images
        addItem("Organic Honeycrisp Apples", "Fresh Farms", 2.99, 50, "Crisp, sweet, and locally grown honeycrisp apples.", produce, "https://images.unsplash.com/photo-1560806887-1e4cd0b6cbd6?auto=format&fit=crop&w=500&q=80");
        addItem("Whole Milk (1 Gallon)", "Dairy Gold", 3.89, 20, "Pasteurized Grade A whole milk.", dairy, "https://images.unsplash.com/photo-1550583724-b2692b85b150?auto=format&fit=crop&w=500&q=80");
        addItem("Plain Whole Milk Greek Yogurt (32 oz)", "Chobani", 4.99, 30, "Rich, creamy, non-GMO plain whole milk Greek yogurt.", dairy, "https://images.unsplash.com/photo-1488477181946-6428a0291777?auto=format&fit=crop&w=500&q=80");
        addItem("Fresh Organic Bananas (Bunch)", "Dole", 1.89, 45, "Sweet, naturally ripened organic bananas.", produce, "https://images.unsplash.com/photo-1571771894821-ce9b6c11b08e?auto=format&fit=crop&w=500&q=80");
        addItem("100% Whole Wheat Bread", "Nature's Own", 2.79, 25, "Soft, wholesome 100% whole wheat bread.", bakery, "https://images.unsplash.com/photo-1509440159596-0249088772ff?auto=format&fit=crop&w=500&q=80");
        addItem("Extra Virgin Olive Oil (16.9 oz)", "California Olive Ranch", 8.99, 15, "First cold-pressed extra virgin olive oil.", pantry, "https://images.unsplash.com/photo-1474979266404-7eaacbcd87c5?auto=format&fit=crop&w=500&q=80");
        addItem("Hass Avocados (4-Pack)", "Avocados from Mexico", 3.99, 35, "Creamy, fresh Hass avocados.", produce, "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?auto=format&fit=crop&w=500&q=80");

        System.out.println("Sample Item/Inventory data successfully initialized!");
    }

    private void addItem(String name, String brand, double price, int stock, String desc, Category cat, String imgUrl) {
        Item item = new Item();
        item.setName(name);
        item.setBrandSupplier(brand);
        item.setPrice(price);
        item.setAvailableStock(stock);
        item.setDescription(desc);
        item.setCategory(cat);
        item = itemRepository.save(item);

        itemImageRepository.save(new ItemImage(imgUrl, item));
        itemService.updateStock(item.getId(), 0, "restock", 1001L);
    }
}
