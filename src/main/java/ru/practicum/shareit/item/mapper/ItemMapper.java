package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item itemFromItemDto(ItemDto itemDto);

    ItemDto itemDtoFromItem(Item item);

    /*public ItemDto toItemDto(Item item) {
        return new ItemDto(item.getId(), item.getName(),
                item.getDescription(), item.getAvailable());
    }

    public Item toItem(ItemDto item, User owner) {
        return new Item(item.getId(), item.getName(), item.getDescription(), item.getAvailable(), owner, itemRequest);
    }*/
}
