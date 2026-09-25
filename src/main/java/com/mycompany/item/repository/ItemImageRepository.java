package com.mycompany.item.repository;

import com.mycompany.item.model.ItemImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemImageRepository extends JpaRepository<ItemImage, Long> {}
