package com.hanghae.module_item.item.service;

import com.hanghae.module_item.client.UserClient;
import com.hanghae.module_item.client.dto.GetUserRoleResponse;
import com.hanghae.module_item.item.dto.request.CreateItemRequest;
import com.hanghae.module_item.item.dto.request.ReduceStockRequest;
import com.hanghae.module_item.item.dto.response.CreateItemResponse;
import com.hanghae.module_item.item.dto.response.ItemDetailsResponse;
import com.hanghae.module_item.item.dto.response.StockResponse;
import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.entity.Stock;
import com.hanghae.module_item.item.repository.ItemRepository;
import com.hanghae.module_item.item.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
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
            throw new IllegalArgumentException("only seller can sell items");
        }

        Item item = Item.builder()
                .sellerNum(createItemRequest.getSellerNum())
                .title(createItemRequest.getTitle())
                .description(createItemRequest.getDescription())
                .price(createItemRequest.getPrice())
                .availableAt(createItemRequest.getAvailableAt())
                .endAt(createItemRequest.getEndAt())
                .build();

        Item newItem = itemRepository.save(item);
        Stock stock = Stock.builder()
                .itemNum(newItem.getItemNum())
                .stock(createItemRequest.getStock())
                .build();

        stockRepository.save(stock);

        return new CreateItemResponse(newItem, stock);
    }

    @Transactional
    public List<String> getAllItems(){
        List<String> titles = itemRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(Item::getTitle)
                .collect(Collectors.toList());

        return titles;
    }

    @Transactional
    public ItemDetailsResponse getItemDetails(Long itemNum){
        Item item = itemRepository.findById(itemNum)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemNum));

        Stock stocks = stockRepository.findById(itemNum)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id(stock table)" + itemNum));

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

    @Transactional
    public Long getItemStocks(Long itemNum) {
        Stock stocks = stockRepository.findById(itemNum)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: "  + itemNum));

        return stocks.getStock();
    }

    @Transactional
    public synchronized StockResponse reduceItemStocks(ReduceStockRequest reduceStockRequest) {
        Long itemNum = reduceStockRequest.getItemNum();
        Long quantity = reduceStockRequest.getQuantity();

        Stock itemStock = stockRepository.findAndLockById(itemNum);

        if (itemStock == null) {
            // Null인 경우를 처리하세요, 예를 들어 예외를 던지거나 에러 응답을 반환
            throw new IllegalArgumentException("cant find item");
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

}
