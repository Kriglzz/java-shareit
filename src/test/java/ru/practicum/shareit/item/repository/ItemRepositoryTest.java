package ru.practicum.shareit.item.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    Item item1;
    Item item2;
    Item item3;
    User user1;
    User user2;
    Pageable pageable;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "item1", "description1",
                true, user1, null, null));
        item2 = itemRepository.save(new Item(2L, "item2", "description2",
                true, user2, null, null));
        item3 = itemRepository.save(new Item(3L, "item3", "description3",
                true, user2, null, null));
        pageable = PageRequest.of(0, 10);
    }
    @Test
    void testFindAllByOwnerId() {
        List<Item> itemsUser1 = itemRepository.findAllByOwnerId(user1.getId(), pageable);
        List<Item> itemsUser2 = itemRepository.findAllByOwnerId(user2.getId(), pageable);

        assertThat(itemsUser1).hasSize(1).contains(item1);
        assertThat(itemsUser2).hasSize(2).contains(item2, item3);
    }

    @Test
    void testFindByNameOrDescriptionContainingIgnoreCase() {
        List<Item> items = itemRepository.findByNameOrDescriptionContainingIgnoreCase("item", pageable);

        assertThat(items).hasSize(3).contains(item1, item2, item3);

        items = itemRepository.findByNameOrDescriptionContainingIgnoreCase("description1", pageable);

        assertThat(items).hasSize(1).contains(item1);

        items = itemRepository.findByNameOrDescriptionContainingIgnoreCase("description2", pageable);

        assertThat(items).hasSize(1).contains(item2);

        items = itemRepository.findByNameOrDescriptionContainingIgnoreCase("description3", pageable);

        assertThat(items).hasSize(1).contains(item3);
    }
    @Test
    public void testFindAllByItemRequestId() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setCreated(LocalDateTime.now());
        entityManager.persist(itemRequest);
        entityManager.flush();

        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setItemRequest(itemRequest);
        entityManager.persist(item);
        entityManager.flush();

        List<Item> foundItems = itemRepository.findAllByItemRequestId(itemRequest.getId());

        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.get(0).getName()).isEqualTo(item.getName());
    }
}