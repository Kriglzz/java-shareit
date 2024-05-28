package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public interface ItemMapper {
    Item itemFromItemDto(ItemDto itemDto);

    ItemDto itemDtoFromItem(Item item);

    /*static ItemDto toItemDtoRequest(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getItemRequest() != null ? item.getItemRequest().getId() : null);
    }*/
}
