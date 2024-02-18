package com.hanghae.module_item.item.repository;

import com.hanghae.module_item.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByDeletedAtIsNull();

}
