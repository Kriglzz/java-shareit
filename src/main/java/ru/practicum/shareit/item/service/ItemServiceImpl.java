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
    /*    private final Map<Long, Item> items = new HashMap<>();
        private final Map<Long, List<ItemDto>> usersItems = new HashMap<>();*/
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

   /* @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto item) {
        checkId(itemId);
        checkXShare(itemId, userId);
        Item updated = items.get(itemId);
        if (item.getName() != null) {
            updated.setName(item.getName());
        }
        if (item.getDescription() != null) {
            updated.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            updated.setAvailable(item.getAvailable());
        }
        usersItems.values().stream()
                .filter(list -> list.stream().anyMatch(i -> i.getId().equals(itemId)))
                .forEach(list -> list.stream()
                        .filter(i -> i.getId().equals(itemId))
                        .forEach(i -> {
                            i.setName(updated.getName());
                            i.setDescription(updated.getDescription());
                            i.setAvailable(updated.getAvailable());
                        })
                );
        return itemMapper.itemDtoFromItem(updated);
    }*/

    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        checkXShare(item.getId(), userId);
        updateItemFields(item, itemDto);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.itemDtoFromItem(updatedItem);
    }

    /*@Override
    public ItemDto getItemById(Long itemId) {
        checkId(itemId);
        return itemMapper.itemDtoFromItem(items.get(itemId));
    }*/

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        return itemMapper.itemDtoFromItem(item);
    }

    /*@Override
    public void deleteItemById(Long itemId) {
        checkId(itemId);
        items.remove(itemId);
    }*/

    @Override
    public void deleteItemById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    /*@Override
    public List<ItemDto> getAllItems() {
        return items.values().stream()
                .map(itemMapper::itemDtoFromItem)
                .collect(Collectors.toList());
    }*/

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> allItems = itemRepository.findAll();
        return allItems.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    /*@Override
    public List<ItemDto> getAllUserItems(Long userId) {
        return usersItems.get(userId);
    }*/

    @Override
    public List<ItemDto> getAllUserItems(Long userId, Sort sort) {
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, sort);
        return userItems.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    /*@Override
    public List<ItemDto> search(String text) {
        return items.values().stream()
                .filter(itemDto -> !text.isBlank() &&
                        (itemDto.getName().toLowerCase().contains(text.toLowerCase()) ||
                                itemDto.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        itemDto.getAvailable().equals(true))
                .map(itemMapper::itemDtoFromItem)
                .collect(Collectors.toList());
    }*/

    @Override
    public List<ItemDto> search(String text) {
        List<Item> searchResults = itemRepository.findByNameOrDescriptionContainingIgnoreCase(text);
        return searchResults.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    private void checkAvailable(ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Некорректноые данные. Проверьте статус \"available\"");
        }
    }

    /*private void checkId(Long itemId) {
        if (items.values().stream().noneMatch((user) -> Objects.equals(user.getId(), itemId))) {
            throw new NotFoundException("Вещь с id \"" + itemId + "\" не найдена");
        }
    }*/

    /*private void checkXShare(Long itemId, Long userId) {
        boolean itemFound = false;
        for (Map.Entry<Long, List<ItemDto>> entry : usersItems.entrySet()) {
            if (entry.getValue().stream().anyMatch(item -> item.getId().equals(itemId))) {
                itemFound = true;
                if (!entry.getKey().equals(userId)) {
                    throw new NotFoundException("Предмет с id " + itemId + " не принадлежит пользователю с id " + userId);
                }
            }
        }
    }*/

    private void checkXShare(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Предмет с id \"" + itemId +
                    "\" не принадлежит пользователю с id \"" + userId + "\"");
        }
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
}
