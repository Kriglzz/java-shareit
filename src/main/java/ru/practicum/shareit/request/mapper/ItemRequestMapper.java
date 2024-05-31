package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemRequestMapper {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

    public ItemRequest itemRequestFromItemRequestDto(ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(itemRequestDto.getId());
        itemRequest.setCreated(itemRequestDto.getCreated());
        itemRequest.setDescription(itemRequestDto.getDescription());
        return itemRequest;
    }

    public ItemRequestDto itemRequestDtoFromItemRequest(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setId(itemRequest.getId());
        itemRequestDto.setCreated(itemRequest.getCreated());
        itemRequestDto.setDescription(itemRequest.getDescription());
        itemRequestDto.setRequester(itemRequest.getRequester().getId());
        return itemRequestDto;
    }

    public ItemRequestDto addItems(ItemRequestDto itemRequestDto) {
        List<Item> items = itemRepository.findAllByItemRequestId(itemRequestDto.getId());
        List<ItemDto> itemDtos = items.stream()
                .map(item -> {
                    ItemDto itemDto = itemMapper.itemDtoFromItem(item);
                    itemDto.setRequestId(item.getItemRequest().getId());
                    return itemDto;
                })
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemDtos);
        return itemRequestDto;
    }

}
