package com.hanghae.module_item.item.entity;

import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.request.UpdateItemRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class ItemTest {

    @Test
    @DisplayName("상품 생성")
    void createItemFromRequest() {
        // Given
        CreateItemRequest request = new CreateItemRequest(1L, "testItem", "testItemDescription", BigDecimal.valueOf(1000), 10L, null, null);

        // When
        Item item = Item.create(request);

        // Then
        assertNotNull(item);
        assertEquals(request.getSellerNum(), item.getSellerNum());
        assertEquals(request.getTitle(), item.getTitle());
        assertEquals(request.getDescription(), item.getDescription());
        assertEquals(request.getPrice(), item.getPrice());
        assertEquals(request.getAvailableAt(), item.getAvailableAt());
        assertEquals(request.getEndAt(), item.getEndAt());
    }

    @Test
    @DisplayName("상품 정보 수정")
    void updateItemFromRequest() {
        // Given
        Item item = new Item(1L, "oldTitle", "oldDescription", BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
        UpdateItemRequest request = new UpdateItemRequest("New Title", "New Description", BigDecimal.valueOf(900), LocalDateTime.now(), LocalDateTime.now().plusDays(2));

        // When
        item.update(request);

        // Then
        assertEquals(request.getTitle(), item.getTitle());
        assertEquals(request.getDescription(), item.getDescription());
        assertEquals(request.getPrice(), item.getPrice());
        assertEquals(request.getAvailableAt(), item.getAvailableAt());
        assertEquals(request.getEndAt(), item.getEndAt());
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteItem() {
        // Given
        Item item = new Item(1L, "Item", "Description", BigDecimal.valueOf(100), LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        // When
        item.delete();

        // Then
        assertNotNull(item.getDeletedAt());
    }



}