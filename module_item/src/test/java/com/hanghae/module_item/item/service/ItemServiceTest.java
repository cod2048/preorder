package com.hanghae.module_item.item.service;

import com.hanghae.module_item.client.StockClient;
import com.hanghae.module_item.client.UserClient;
import com.hanghae.module_item.client.dto.GetUserRoleResponse;
import com.hanghae.module_item.client.dto.StockDto;
import com.hanghae.module_item.common.exception.CustomException;
import com.hanghae.module_item.common.exception.ErrorCode;
import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.request.UpdateItemRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.repository.ItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private StockClient stockClient;

    @Mock
    private UserClient userClient;

    @Nested
    @DisplayName("아이템 생성")
    class createItem {

        @Test
        @DisplayName("아이템 생성 성공")
        void createItemSuccess() {
            // Given
            GetUserRoleResponse getUserRoleResponse = new GetUserRoleResponse(1L, "SELLER");
            CreateItemRequest request = new CreateItemRequest(1L, "newItem", "newItemDescription", BigDecimal.valueOf(1000), 10L, null, null);
            Item item = new Item(1L, "newItem", "newItemDescription", BigDecimal.valueOf(1000), null, null);
            when(userClient.getUserRole(anyLong())).thenReturn(getUserRoleResponse);
            when(itemRepository.save(any(Item.class))).thenReturn(item);

            // When
            CreateItemResponse response = itemService.create(request);

            // Then
            assertNotNull(response);
            assertEquals(request.getTitle(), response.getItem().getTitle());
            assertEquals(request.getDescription(), response.getItem().getDescription());
            assertEquals(request.getPrice(), response.getItem().getPrice());
        }


        @Test
        @DisplayName("아이템 생성 실패 - 판매자 검증 실패")
        void createItemFailWhenNotSeller() {
            // Given
            GetUserRoleResponse getUserRoleResponse = new GetUserRoleResponse(1L, "BUYER");
            CreateItemRequest request = new CreateItemRequest(1L, "newItem", "newItemDescription", BigDecimal.valueOf(1000), 10L, null, null);
            when(userClient.getUserRole(anyLong())).thenReturn(getUserRoleResponse);

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                itemService.create(request);
            });

            //Then
            assertEquals(ErrorCode.NOT_SELLER, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("아이템 정보 조회")
    class getItems {
        @Test
        @DisplayName("모든 아이템 조회")
        void getAllItems() {
            // Given
            Item item1 = new Item(1L, "Item 1", "Description 1", BigDecimal.valueOf(1000), null, null);
            Item item2 = new Item(2L, "Item 2", "Description 2", BigDecimal.valueOf(2000), null, null);
            List<Item> mockItems = Arrays.asList(item1, item2);
            when(itemRepository.findAllByDeletedAtIsNull()).thenReturn(mockItems);

            // When
            List<String> titles = itemService.getAllItems();

            // Then
            List<String> expectedTitles = mockItems.stream().map(Item::getTitle).toList();
            assertEquals(expectedTitles, titles);
        }

        @Test
        @DisplayName("아이템 정보 상세 조회 성공")
        void getItemDetailsSuccess() {
            // Given
            Long itemNum = 1L;
            Item mockItem = new Item(1L, "Item1", "Item1Description", BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
            StockDto mockStock = new StockDto(1L, 10L);
            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(mockItem));
            when(stockClient.getStocks(itemNum)).thenReturn(mockStock);

            // When
            ItemDetailsResponse response = itemService.getItemDetails(itemNum);

            // Then
            assertNotNull(response);
            assertEquals(mockItem.getTitle(), response.getTitle());
            assertEquals(mockStock.getStock(), response.getStock());
        }

        @Test
        @DisplayName("아이템 정보 상세 조회 실패 - 아이템 미존재")
        void getItemDetailsItemNotFound() {
            // Given
            Long itemNum = 1L;
            when(itemRepository.findById(itemNum)).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                itemService.getItemDetails(itemNum);
            });

            //Then
            assertEquals(ErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("아이템 세부 정보 조회 실패 - 아이템 삭제됨")
        void getItemDetailsItemDeleted() {
            // Given
            Long itemNum = 1L;
            Item deletedItem = new Item(1L, "Item1", "Item1Description", BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
            deletedItem.delete();

            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(deletedItem));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                itemService.getItemDetails(itemNum);
            });

            //Then
            assertEquals(ErrorCode.DELETED_ITEM, exception.getErrorCode());
        }

    }

    @Nested
    @DisplayName("아이템 정보 수정")
    class updateItem {
        @Test
        @DisplayName("아이템 업데이트 성공")
        void updateItemSuccess() {
            // Given
            Long itemNum = 1L;
            UpdateItemRequest updateItemRequest = new UpdateItemRequest("Updated Title", "Updated Description", BigDecimal.valueOf(2000), LocalDateTime.now(), LocalDateTime.now().plusDays(10));
            Item mockItem = new Item(1L, "Original Title", "Original Description", BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
            StockDto mockStock = new StockDto(1L, 20L);

            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(mockItem));
            when(stockClient.getStocks(itemNum)).thenReturn(mockStock);

            // When
            ItemDetailsResponse response = itemService.update(itemNum, updateItemRequest);

            // Then
            assertNotNull(response);
            assertEquals(updateItemRequest.getTitle(), response.getTitle());
            assertEquals(updateItemRequest.getDescription(), response.getDescription());
            assertEquals(updateItemRequest.getPrice(), response.getPrice());
            assertEquals(mockStock.getStock(), response.getStock());
        }

        @Test
        @DisplayName("아이템 업데이트 실패 - 아이템 미존재")
        void updateItemNotFound() {
            // Given
            Long itemNum = 999L;
            UpdateItemRequest updateItemRequest = new UpdateItemRequest("Title", "Description", BigDecimal.valueOf(2000), LocalDateTime.now(), LocalDateTime.now().plusDays(10));

            when(itemRepository.findById(itemNum)).thenReturn(Optional.empty());


            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                itemService.update(itemNum, updateItemRequest);
            });

            //Then
            assertEquals(ErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("아이템 업데이트 실패 - 삭제 된 아이템")
        void updateItemDeleted() {
            // Given
            Long itemNum = 1L;
            UpdateItemRequest updateItemRequest = new UpdateItemRequest("Title", "Description", BigDecimal.valueOf(2000), LocalDateTime.now(), LocalDateTime.now().plusDays(10));
            Item deletedItem = new Item(1L, "Deleted Item", "Description", BigDecimal.valueOf(1000), LocalDateTime.now(), LocalDateTime.now().plusDays(5));
            deletedItem.delete();

            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(deletedItem));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> {
                itemService.update(itemNum, updateItemRequest);
            });

            // Then
            assertEquals(ErrorCode.DELETED_ITEM, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("아이템 삭제")
    class deleteItem {
        @Test
        @DisplayName("아이템 삭제 성공")
        void deleteItemSuccess() {
            // Given
            Long itemNum = 1L;
            Item mockItem = new Item(1L, "Item", "Item Description", BigDecimal.valueOf(1000), null, null);
            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(mockItem));
            doNothing().when(stockClient).deleteStocks(itemNum);

            // When
            ItemDetailsResponse response = itemService.delete(itemNum);

            // Then
            assertNotNull(response);
            assertNotNull(mockItem.getDeletedAt());
            verify(stockClient).deleteStocks(itemNum);
        }

        @Test
        @DisplayName("아이템 삭제 실패 - 아이템 미존재")
        void deleteItemNotFound() {
            // Given
            Long itemNum = 999L;
            when(itemRepository.findById(itemNum)).thenReturn(Optional.empty());

            // When
            CustomException exception = assertThrows(CustomException.class, () -> itemService.delete(itemNum));

            // THen
            assertEquals(ErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());
        }

        @Test
        @DisplayName("아이템 삭제 실패 - 아이템 이미 삭제됨")
        void deleteItemAlreadyDeleted() {
            // Given
            Long itemNum = 1L;
            Item mockItem = new Item(1L, "Item", "Item Description", BigDecimal.valueOf(1000), null, null);
            mockItem.delete();
            when(itemRepository.findById(itemNum)).thenReturn(Optional.of(mockItem));

            // When
            CustomException exception = assertThrows(CustomException.class, () -> itemService.delete(itemNum));

            // Then
            assertEquals(ErrorCode.DELETED_ITEM, exception.getErrorCode());
        }




    }

}