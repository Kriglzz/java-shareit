package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentMapperTest {

    private User user;
    private Item item;
    private ItemRequest itemRequest;

    @Test
    public void testCommentFromCommentDto() {

        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test Comment Text");

        CommentMapper commentMapper = new CommentMapper();

        Comment comment = commentMapper.commentFromCommentDto(commentDto);

        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
    }

    @Test
    public void testCommentDtoFromComment() {

        user = new User(1L, "user1", "user1@mail.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        item = new Item(1L, "item1", "description1", true, user, itemRequest, null);
        Comment comment = new Comment();
        comment.setItem(item);
        comment.setId(1L);
        comment.setText("Test Comment Text");
        comment.setUser(user);

        CommentMapper commentMapper = new CommentMapper();

        CommentDto commentDto = commentMapper.commentDtoFromComment(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
    }
}
