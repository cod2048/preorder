package com.hanghae.module_item.item.service;

import com.hanghae.module_item.client.UserClient;
import com.hanghae.module_item.client.dto.GetUserRoleResponse;
import com.hanghae.module_item.common.exception.CustomException;
import com.hanghae.module_item.common.exception.ErrorCode;
import com.hanghae.module_item.client.StockClient;
import com.hanghae.module_item.client.dto.StockDto;
import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.request.UpdateItemRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.repository.ItemRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserClient userClient;
    private final StockClient stockClient;

    public ItemService(ItemRepository itemRepository, UserClient userClient, StockClient stockClient) {
        this.itemRepository = itemRepository;
        this.userClient = userClient;
        this.stockClient = stockClient;
    }

    @Transactional
    public CreateItemResponse create(CreateItemRequest createItemRequest) {
        GetUserRoleResponse getUserRoleResponse = userClient.getUserRole(createItemRequest.getSellerNum());

        if(!Objects.equals(getUserRoleResponse.getUserRole(), "SELLER")) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }

        Item newitem = Item.create(createItemRequest);

        Item savedItem = itemRepository.save(newitem);

//        log.info("itemService before make stockRequest : {}", createItemRequest.getStock());

        StockDto stockRequest = new StockDto(savedItem.getItemNum(), createItemRequest.getStock());
//        log.info("itemService after make stockRequest : {}", stockRequest.getStock());
        StockDto stockResponse = stockClient.createStocks(stockRequest);

        return new CreateItemResponse(savedItem, stockResponse);
    }

    public List<String> getAllItems(){

        return itemRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(Item::getTitle)
                .collect(Collectors.toList());
    }

    public ItemDetailsResponse getItemDetails(Long itemNum){
        Item item = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (item.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        StockDto stocks = stockClient.getStocks(itemNum);

        return new ItemDetailsResponse(
                item.getItemNum(),
                item.getSellerNum(),
                item.getTitle(),
                item.getDescription(),
                item.getPrice(),
                stocks.getStock(),
                item.getAvailableAt(),
                item.getEndAt()
        );
    }

    public StockDto getItemStocks(Long itemNum) {

        return stockClient.getStocks(itemNum);
    }

    @Transactional
    public ItemDetailsResponse update(Long itemNum, UpdateItemRequest updateItemRequest) {
        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        StockDto targetStock = stockClient.getStocks(itemNum);

        targetItem.update(updateItemRequest);

        return new ItemDetailsResponse(
                targetItem.getItemNum(),
                targetItem.getSellerNum(),
                targetItem.getTitle(),
                targetItem.getDescription(),
                targetItem.getPrice(),
                targetStock.getStock(),
                targetItem.getAvailableAt(),
                targetItem.getEndAt());
    }

    @Transactional
    public ItemDetailsResponse delete(Long itemNum) {
        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        stockClient.deleteStocks(itemNum);
        targetItem.delete();

        return new ItemDetailsResponse(
                targetItem.getItemNum(),
                targetItem.getSellerNum(),
                targetItem.getTitle(),
                targetItem.getDescription(),
                targetItem.getPrice(),
                null,
                targetItem.getAvailableAt(),
                targetItem.getEndAt()
        );
    }
}
