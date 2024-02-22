package com.hanghae.module_item.item.service;

import com.hanghae.module_item.client.UserClient;
import com.hanghae.module_item.client.dto.GetUserRoleResponse;
import com.hanghae.module_item.common.exception.CustomException;
import com.hanghae.module_item.common.exception.ErrorCode;
import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.request.UpdateStockRequest;
import com.hanghae.module_item.item.dto.request.UpdateItemRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
import com.hanghae.module_item.item.dto.response.StockResponse;
import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.entity.Stock;
import com.hanghae.module_item.item.repository.ItemRepository;
import com.hanghae.module_item.item.repository.StockRepository;
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
    private final StockRepository stockRepository;
    private final UserClient userClient;

    public ItemService(ItemRepository itemRepository, StockRepository stockRepository, UserClient userClient) {
        this.itemRepository = itemRepository;
        this.stockRepository = stockRepository;
        this.userClient = userClient;
    }

    @Transactional
    public CreateItemResponse create(CreateItemRequest createItemRequest) {
        GetUserRoleResponse getUserRoleResponse = userClient.getUserRole(createItemRequest.getSellerNum());

        if(!Objects.equals(getUserRoleResponse.getUserRole(), "SELLER")) {
            throw new CustomException(ErrorCode.NOT_SELLER);
        }

        Item newitem = Item.create(createItemRequest);

        Item savedItem = itemRepository.save(newitem);

        Stock stock = Stock.create(savedItem.getItemNum(), createItemRequest.getStock());

        Stock savedStock = stockRepository.save(stock);

        return new CreateItemResponse(savedItem, savedStock);
    }

    public List<String> getAllItems(){
        List<String> titles = itemRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(Item::getTitle)
                .collect(Collectors.toList());

        return titles;
    }

    public ItemDetailsResponse getItemDetails(Long itemNum){
        Item item = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (item.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        Stock stocks = stockRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND));

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

    public Long getItemStocks(Long itemNum) {
        Stock stocks = stockRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND));

        return stocks.getStock();
    }

    @Transactional
    public synchronized StockResponse reduceItemStocks(UpdateStockRequest updateStockRequest) {
        Long itemNum = updateStockRequest.getItemNum();
        Long quantity = updateStockRequest.getQuantity();

        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(()-> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        Stock itemStock = stockRepository.findAndLockById(itemNum);

        if (itemStock == null) {
            throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
        }

        Long currentStock = itemStock.getStock();

        if (currentStock < quantity) {
            log.info("not enough stocks");
        } else {
            Long updateStock = currentStock - quantity;
            itemStock.updateStocks(updateStock);

        }
        return new StockResponse(itemNum, itemStock.getStock());
    }

    @Transactional
    public synchronized StockResponse increaseItemStocks(UpdateStockRequest updateStockRequest) {
        Long itemNum = updateStockRequest.getItemNum();
        Long quantity = updateStockRequest.getQuantity();

        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(()-> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        Stock itemStock = stockRepository.findAndLockById(itemNum);

        if (itemStock == null) {
            throw new CustomException(ErrorCode.ITEM_STOCK_NOT_FOUND);
        }

        itemStock.updateStocks(itemStock.getStock() + quantity);

        return new StockResponse(itemNum, itemStock.getStock());
    }

    @Transactional
    public ItemDetailsResponse update(Long itemNum, UpdateItemRequest updateItemRequest) {
        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        Stock targetStock = stockRepository.findById(itemNum).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        targetItem.update(updateItemRequest);

        return new ItemDetailsResponse(targetItem.getItemNum(), targetItem.getSellerNum(), targetItem.getTitle(), targetItem.getDescription(), targetItem.getPrice(), targetStock.getStock(), targetItem.getAvailableAt(), targetItem.getEndAt());
    }

    @Transactional
    public ItemDetailsResponse delete(Long itemNum) {
        Item targetItem = itemRepository.findById(itemNum)
                .orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        if (targetItem.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.DELETED_ITEM);
        }

        Stock targetStock = stockRepository.findById(itemNum).orElseThrow(() -> new CustomException(ErrorCode.ITEM_NOT_FOUND));

        targetItem.delete();

        return new ItemDetailsResponse(
                targetItem.getItemNum(),
                targetItem.getSellerNum(),
                targetItem.getTitle(),
                targetItem.getDescription(),
                targetItem.getPrice(),
                targetStock.getStock(),
                targetItem.getAvailableAt(),
                targetItem.getEndAt()
        );
    }
}
