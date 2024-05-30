package ru.practicum.shareit.item.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindAllByItemRequestId() {
        // Создаем и сохраняем ItemRequest
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Test Request");
        itemRequest.setCreated(LocalDateTime.now());
        entityManager.persist(itemRequest);
        entityManager.flush();

        // Создаем и сохраняем Item с ссылкой на ItemRequest
        Item item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setItemRequest(itemRequest);
        entityManager.persist(item);
        entityManager.flush();

        // Выполняем запрос
        List<Item> foundItems = itemRepository.findAllByItemRequestId(itemRequest.getId());

        // Проверяем результаты
        assertThat(foundItems).isNotEmpty();
        assertThat(foundItems.get(0).getName()).isEqualTo(item.getName());
    }
}