package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkAvailable(itemDto);
        UserDto owner = userService.getUserById(userId);
        Item item = itemMapper.itemFromItemDto(itemDto);
        item.setOwner(userMapper.userFromUserDto(owner));
        Item savedItem = itemRepository.save(item);
        return itemMapper.itemDtoFromItem(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        checkXShare(item.getId(), userId);
        updateItemFields(item, itemDto);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.itemDtoFromItem(updatedItem);
    }

    private void updateItemFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        return itemMapper.itemDtoFromItem(item);
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> allItems = itemRepository.findAll();
        return allItems.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId, Sort sort) {
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, sort);
        return userItems.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        String searchText = text.toLowerCase();
        List<Item> searchResults = itemRepository.findByNameOrDescriptionContainingIgnoreCase(searchText);

        return searchResults.stream()
                .filter(Item::getAvailable) // Фильтруем по доступности
                .map(itemMapper::itemDtoFromItem)
                .collect(Collectors.toList());
    }

    private void checkAvailable(ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Некорректноые данные. Проверьте статус \"available\"");
        }
    }

    private void checkXShare(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Предмет с id \"" + itemId +
                    "\" не принадлежит пользователю с id \"" + userId + "\"");
        }
    }
}
