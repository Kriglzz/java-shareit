package ru.practicum.shareit.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private UserRepository userRepository;

    Item item1;
    User user1;
    User user2;
    User user3;
    Comment comment1;
    Comment comment2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
        user3 = userRepository.save(new User(3L, "user3", "user3@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "item1", "description1",
                true, user1, null, null));
        comment1 = commentRepository.save(new Comment(1L, "comment1", item1, user2, LocalDateTime.now()));
        comment2 = commentRepository.save(new Comment(2L, "comment2", item1, user3, LocalDateTime.now()));
    }

    @Test
    public void testFindAllByItem() {
        List<Comment> comments = commentRepository.findAllByItem(item1);
        assertThat(comments).containsExactly(comment1, comment2);
    }

}
