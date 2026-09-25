package com.grocerymanagementsys.item.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(length = 1000)
    private String description;

    private int availableStock;
    private String brand;
    private double price;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<InventoryUpdate> inventoryUpdates = new ArrayList<>();

    public Item() {}

    public Item(String name, Category category, String description, int availableStock, String brand, double price) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.availableStock = availableStock;
        this.brand = brand;
        this.price = price;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getAvailableStock() { return availableStock; }
    public void setAvailableStock(int availableStock) { this.availableStock = availableStock; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public List<ItemImage> getImages() { return images; }
    public void setImages(List<ItemImage> images) { this.images = images; }

    public List<InventoryUpdate> getInventoryUpdates() { return inventoryUpdates; }
    public void setInventoryUpdates(List<InventoryUpdate> inventoryUpdates) { this.inventoryUpdates = inventoryUpdates; }
}
