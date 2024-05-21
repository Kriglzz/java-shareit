package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;

public class CommentMapper {

    public Comment commentFromCommentDto(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setText(commentDto.getText());
        return comment;
    }

    public CommentDto commentDtoFromComment(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItem(comment.getItem().getId());
        commentDto.setAuthorName(comment.getAuthorId().getName());
        commentDto.setCreated(comment.getCreated());
        return commentDto;
    }
}
