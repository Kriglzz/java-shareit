package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long itemId, Long userId, ItemDto item);

    ItemDto getItemById(Long itemId);

    void deleteItemById(Long itemId);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllUserItems(Long userId);

    List<ItemDto> search(String text);
}
