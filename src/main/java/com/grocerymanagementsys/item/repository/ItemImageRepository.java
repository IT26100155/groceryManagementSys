package com.grocerymanagementsys.item.repository;

import com.grocerymanagementsys.item.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {
}
