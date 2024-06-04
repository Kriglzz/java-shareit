package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto item);

    ItemDto updateItem(Long itemId, Long userId, ItemDto item);

    ItemDto getItemById(Long userId, Long itemId);

    void deleteItemById(Long itemId);

    List<ItemDto> getAllUserItems(Long userId, Pageable pageable);

    List<ItemDto> search(String text, Pageable pageable);

    CommentDto addComment(Long itemId, Long userId, CommentDto commentDto);

}
