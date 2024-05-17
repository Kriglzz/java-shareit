package ru.practicum.shareit.item.comment.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    Comment commentFromCommentDto(ItemDto itemDto);

    CommentDto commentDtoFromComment(Item item);

}
