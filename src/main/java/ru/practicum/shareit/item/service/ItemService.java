package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long itemId, Long userId, ItemDto item);

    ItemDto getItemById(Long itemId);

    void deleteItemById(Long itemId);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllUserItems(Long userId, Sort sort);

    List<ItemDto> search(String text);
}
