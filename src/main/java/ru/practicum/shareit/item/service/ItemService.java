package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long itemId, Long userId, ItemDto item);

    ItemDto getItemById(Long userId, long itemId);

    void deleteItemById(Long itemId);

    List<ItemDto> getAllItems();

    List<ItemDto> getAllUserItems(Long userId, Sort sort);

    List<ItemDto> search(String text);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

}