package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private ItemRequest itemRequest;
    @BeforeEach
    public void setUp() {

        user = new User(1L, "user1", "user1@mail.ru");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        item = new Item(1L, "item1", "description1", true, user, itemRequest, null);
        itemDto = new ItemDto(1L, "item1", "description1",
                true, null, null, null, null);
    }
    @Test
    public void testAddItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemMapper.itemFromItemDto(itemDto)).thenReturn(item);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.itemDtoFromItem(item)).thenReturn(itemDto);
        ItemDto result = itemService.addItem(1L, itemDto);

        assertEquals(itemDto, result);
        verify(userRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).itemFromItemDto(itemDto);
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    public void addItem_userNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(1L, itemDto);
        });

        assertEquals("Пользователь с id \"1\" не найден", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRepository, times(0)).save(any(Item.class));
        verify(itemMapper, times(0)).itemDtoFromItem(any(Item.class));
    }

    @Test
    public void addItem_itemRequestNotFound() {
        itemDto.setRequestId(2L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(2L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.addItem(1L, itemDto);
        });

        assertEquals("Запрос с id 2 не найден!", exception.getMessage());
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).findById(2L);
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    public void testUpdate() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        when(itemMapper.itemDtoFromItem(any(Item.class))).thenReturn(itemDto);

        ItemDto result = itemService.updateItem(1L, 1L, itemDto);

        assertNotNull(result);
        assertEquals("item1", result.getName());
        assertEquals("description1", result.getDescription());
        assertEquals(true, result.getAvailable());

        verify(itemRepository, times(2)).findById(1L);
        verify(itemRepository, times(1)).save(any(Item.class));
        verify(itemMapper, times(1)).itemDtoFromItem(any(Item.class));
    }

    @Test
    public void updateItem_itemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.updateItem(1L, 1L, itemDto);
        });

        assertEquals("Предмет с id \"1\" не найден", exception.getMessage());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testGetItemById() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemMapper.itemDtoFromItem(any(Item.class))).thenReturn(itemDto);
        BookingDto phantomBookingDto = new BookingDto();
        CommentDto phantomCommentDto = new CommentDto();
        when(bookingMapper.bookingDtoFromBooking(any())).thenReturn(phantomBookingDto);
        when(commentMapper.commentDtoFromComment(any())).thenReturn(phantomCommentDto);

        ItemDto result = itemService.getItemById(1L, 1L);

        assertNotNull(result);
        assertEquals("item1", result.getName());
        assertEquals("description1", result.getDescription());
        assertEquals(true, result.getAvailable());

        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(1)).itemDtoFromItem(any(Item.class));
    }

    @Test
    void getItemById_itemNotFound() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> {
            itemService.getItemById(1L, 1L);
        });

        assertEquals("Предмет с id \"1\" не найден", exception.getMessage());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemMapper, times(0)).itemDtoFromItem(any(Item.class));
    }

    @Test
    void deleteItemById_success() {
        doNothing().when(itemRepository).deleteById(anyLong());

        itemService.deleteItemById(1L);

        verify(itemRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetAllUserItems() {

        ItemRequest request = new ItemRequest(1L, "request", new User(), LocalDateTime.now());
        User user = new User(1L, "username", "mail@mail.com");
        Item item = new Item(1L, "item", "description", true, user, request, new Comment());
        Booking approvedBooking = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                item, user, BookingStatus.APPROVED);
        Comment comment = new Comment(1L, "Comment 1", item, user, LocalDateTime.now());
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "start"));


        when(itemRepository.findAllByOwnerId(1L, pageable)).thenReturn(Collections.singletonList(item));
        when(commentRepository.findAllByItem(any())).thenReturn(Collections.singletonList(comment));
/*        when(bookingRepository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                any(), any(), any())).thenReturn(Optional.empty());
        when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                any(), any(), any())).thenReturn(Optional.empty());*/
        when(itemMapper.itemDtoFromItem(item)).thenReturn(new ItemDto());
        BookingDto phantomBookingDto = new BookingDto();
        CommentDto phantomCommentDto = new CommentDto();
        when(bookingMapper.bookingDtoFromBooking(any())).thenReturn(phantomBookingDto);
        when(commentMapper.commentDtoFromComment(any())).thenReturn(phantomCommentDto);


        List<ItemDto> result = itemService.getAllUserItems(1L, pageable);


        assertEquals(1, result.size());
        verify(itemRepository, times(1)).findAllByOwnerId(1L, pageable);
        verify(commentRepository, times(1)).findAllByItem(any());
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                any(), any(), any());
        verify(bookingRepository, times(1)).findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                any(), any(), any());
        verify(itemMapper, times(1)).itemDtoFromItem(any());

    }

    @Test
    void testSearch() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(DESC, "start"));
        when(itemRepository.findByNameOrDescriptionContainingIgnoreCase("test", pageable))
                .thenReturn(Collections.singletonList(item));
        when(itemMapper.itemDtoFromItem(item)).thenReturn(itemDto);
        List<ItemDto> result = itemService.search("test", pageable);


        assertEquals(1, result.size());
        assertEquals(itemDto, result.get(0));

        verify(itemRepository, times(1))
                .findByNameOrDescriptionContainingIgnoreCase("test", pageable);
        verify(itemMapper, times(1)).itemDtoFromItem(item);
    }

    @Test
    void testAddComment() {
        Long userId = 1L;
        Long itemId = 2L;
        CommentDto createdCommentDto = new CommentDto();
        createdCommentDto.setText("test");

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(commentMapper.commentFromCommentDto(createdCommentDto)).thenReturn(new Comment());
        when(commentRepository.save(any(Comment.class))).thenReturn(new Comment());
        when(commentMapper.commentDtoFromComment(any(Comment.class))).thenReturn(createdCommentDto);

        List<Booking> bookings = new ArrayList<>();
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookings.add(booking);
        when(bookingRepository.findByItemAndBookerAndStatus(
                any(), any(), any())).thenReturn(bookings);

        Optional<User> userOptional = userRepository.findById(userId);
        assertTrue(userOptional.isPresent());

        Optional<Item> itemOptional = itemRepository.findById(itemId);
        assertTrue(itemOptional.isPresent());

        CommentDto result = itemService.addComment(itemId, userId, createdCommentDto);

        assertEquals(createdCommentDto.getText(), result.getText());
        verify(userRepository, times(2)).findById(userId);
        verify(itemRepository,times(2)).findById(itemId);
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).commentDtoFromComment(any(Comment.class));
    }


}
