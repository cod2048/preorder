package com.hanghae.module_item.item.service;

import com.hanghae.module_item.item.dto.CreateItemRequest;
import com.hanghae.module_item.item.dto.CreateItemResponse;
import com.hanghae.module_item.item.dto.GetItemResponse;
import com.hanghae.module_item.item.entity.Item;
import com.hanghae.module_item.item.entity.Stock;
import com.hanghae.module_item.item.repository.ItemRepository;
import com.hanghae.module_item.item.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {
    private final ItemRepository itemRepository;
    private final StockRepository stockRepository;

    public ItemService(ItemRepository itemRepository, StockRepository stockRepository) {
        this.itemRepository = itemRepository;
        this.stockRepository = stockRepository;
    }

    @Transactional
    public CreateItemResponse create(CreateItemRequest createItemRequest) {
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

        CreateItemResponse createItemResponse = new CreateItemResponse(newItem, stock);

        return createItemResponse;
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
    public GetItemResponse getItemDetails(Long itemNum){
        Item item = itemRepository.findById(itemNum)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemNum));

        return new GetItemResponse(item.getTitle(), item.getDescription(), item.getPrice());
    }

    @Transactional
    public Long getItemStocks(Long itemNum) {
        Stock stocks = stockRepository.findById(itemNum)
                .orElseThrow(() -> new EntityNotFoundException("Item not found with id: "  + itemNum));

        return stocks.getStock();
    }

}