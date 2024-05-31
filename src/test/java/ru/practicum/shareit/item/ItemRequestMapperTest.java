package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

public class ItemRequestMapperTest {

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testItemRequestFromItemRequestDto() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);
        dto.setCreated(LocalDateTime.now());
        dto.setDescription("Description");

        ItemRequest itemRequest = itemRequestMapper.itemRequestFromItemRequestDto(dto);

        assertEquals(dto.getId(), itemRequest.getId());
        assertEquals(dto.getCreated(), itemRequest.getCreated());
        assertEquals(dto.getDescription(), itemRequest.getDescription());
    }

    @Test
    public void testItemRequestDtoFromItemRequest() {
        User requester = new User();
        requester.setId(1L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Description");
        itemRequest.setRequester(requester);

        ItemRequestDto dto = itemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);

        assertEquals(itemRequest.getId(), dto.getId());
        assertEquals(itemRequest.getCreated(), dto.getCreated());
        assertEquals(itemRequest.getDescription(), dto.getDescription());
        assertEquals(itemRequest.getRequester().getId(), dto.getRequester());
    }

    @Test
    public void testAddItems() {
        ItemRequestDto dto = new ItemRequestDto();
        dto.setId(1L);

        Item item = new Item();
        item.setItemRequest(new ItemRequest());
        item.getItemRequest().setId(1L);

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(1L);

        List<Item> items = Collections.singletonList(item);

        when(itemRepository.findAllByItemRequestId(1L)).thenReturn(items);
        when(itemMapper.itemDtoFromItem(item)).thenReturn(itemDto);

        ItemRequestDto resultDto = itemRequestMapper.addItems(dto);

        assertNotNull(resultDto.getItems());
        assertEquals(1, resultDto.getItems().size());
        assertEquals(itemDto, resultDto.getItems().get(0));
    }
}
