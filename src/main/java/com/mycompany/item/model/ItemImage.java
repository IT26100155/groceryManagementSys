package com.mycompany.item.model;

import jakarta.persistence.*;

@Entity
@Table(name = "item_image")
public class ItemImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    public ItemImage() {}

    public ItemImage(String url, Item item) {
        this.url = url;
        this.item = item;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
}
