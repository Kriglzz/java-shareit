package ru.practicum.shareit.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(source = "item", target = "item.id")
    @Mapping(source = "authorName", target = "author_id.id")
    Comment commentFromCommentDto(CommentDto commentDto);

    @Mapping(source = "item.id", target = "item")
    @Mapping(source = "author_id.id", target = "authorName")
    CommentDto commentDtoFromComment(Comment comment);
}
