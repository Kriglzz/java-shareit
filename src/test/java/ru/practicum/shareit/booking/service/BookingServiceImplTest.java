package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.IllegalStateException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    UserDto ownerDto;
    User owner;
    UserDto bookerDto;
    User booker;

    Item item1;
    Item item2;
    BookingDto bookingDto1;

    BookingDto bookingDto2;

    BookingDto bookingDto3;


    @BeforeEach
    void setUp() {
        ownerDto = new UserDto(
                1L,
                "1user",
                "2user@mail.ru");
        bookerDto = new UserDto(
                2L,
                "2user",
                "2user@mail.ru");
        booker = new User(
                2L,
                "2user",
                "2user@mail.ru");

        ItemDto itemDto1 = new ItemDto(1L, "item1", "descrirption1",
                true, null, bookingDto2, null, null);
        ItemDto itemDto2 = new ItemDto(2L, "item2", "descrirption2",
                true, bookingDto1, bookingDto3, null, null);
        ItemDto itemDto3 = new ItemDto(3L, "item3", "descrirption3",
                true, bookingDto2, null, null, null);

        bookingDto1 = new BookingDto(1L, 1L, 2L, LocalDateTime.now().minusDays(2),
                LocalDateTime.now().minusDays(1), itemDto1, bookerDto, BookingStatus.PAST);
        bookingDto2 = new BookingDto(2L, 1L, 2L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2), itemDto2, bookerDto, BookingStatus.APPROVED);
        bookingDto3 = new BookingDto(3L, 1L, 2L,
                LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(3), itemDto3, bookerDto, BookingStatus.FUTURE);
        owner = new User(
                1L,
                "1user",
                "2user@mail.ru");
        item1 = new Item(1L, "item1", "descrirption1", true,
                owner, null, null);
        item2 = new Item(2L, "item2", "descrirption2", true,
                owner, null, null);
    }

    @Test
    public void testCreateBooking() throws Exception {
        when(itemRepository.findById(bookingDto2.getItemId())).thenReturn(Optional.of(item2));
        when(userRepository.findById(bookerDto.getId())).thenReturn(Optional.of(new User()));
        when(bookingMapper.bookingFromBookingDto(bookingDto2)).thenReturn(new Booking());
        when(bookingRepository.save(any(Booking.class))).thenReturn(new Booking());
        when(bookingMapper.bookingDtoFromBooking(any(Booking.class))).thenReturn(bookingDto2);

        BookingDto result = bookingService.createBooking(bookerDto.getId(), bookingDto2);

        assertEquals(bookingDto2, result);

    }

    @Test
    public void testCreateBookingInvalidDates() {

        bookingDto2.setStart(LocalDateTime.now().plusDays(2));
        bookingDto2.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(IllegalStateException.class, () -> {
            bookingService.createBooking(bookerDto.getId(), bookingDto2);
        });
    }

    @Test
    public void testCreateBookingItemNotFound() {

        when(itemRepository.findById(bookingDto2.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookerDto.getId(), bookingDto2);
        });
    }

    @Test
    public void testCreateBookingUserNotFound() {
        // Arrange
        when(itemRepository.findById(bookingDto2.getItemId())).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(bookerDto.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(bookerDto.getId(), bookingDto2);
        });
    }

    @Test
    public void testCreateBookingOwnerBookingOwnItem() {

        Item item = new Item();
        item.setOwner(owner);
        when(itemRepository.findById(bookingDto2.getItemId())).thenReturn(Optional.of(item));
        when(userRepository.findById(bookerDto.getId())).thenReturn(Optional.of(new User(1L, "1user", "1user@mail.ru")));

        assertThrows(NotFoundException.class, () -> {
            bookingService.createBooking(1L, bookingDto2);
        });
    }

    @Test
    public void testCreateBookingItemNotAvailable() {
        Item item2 = new Item(2L, "item2", "descrirption2", false,
                owner, null, null);

        when(itemRepository.findById(bookingDto2.getItemId())).thenReturn(Optional.of(item2));
        when(userRepository.findById(bookerDto.getId())).thenReturn(Optional.of(new User()));

        assertThrows(ValidationException.class, () -> {
            bookingService.createBooking(bookerDto.getId(), bookingDto2);
        });
    }

    @Test
    public void testUpdateBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item2, booker, BookingStatus.WAITING);
        booking.setItem(new Item(1L, "item2", "description2", true, new User(userId, "owner", "owner@mail.ru"), null, null));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "owner", "owner@mail.ru")));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(new Item()));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);
        when(bookingMapper.bookingDtoFromBooking(booking)).thenReturn(bookingDto2);

        BookingDto result = bookingService.updateBooking(userId, bookingId, approved);

        assertEquals(BookingStatus.APPROVED, result.getStatus());
    }

    @Test
    public void testUpdateBookingUserNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(userId, bookingId, approved);
        });
    }

    @Test
    public void testUpdateBookingNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(userId, bookingId, approved);
        });
    }

    @Test
    public void testUpdateBookingItemNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        Booking booking = new Booking();
        booking.setItem(new Item());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.updateBooking(userId, bookingId, approved);
        });
    }

    @Test
    public void testUpdateBookingAccessDenied() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        Booking booking = new Booking();
        Item item = new Item();
        item.setOwner(new User(2L, "otherOwner", "other@mail.ru"));
        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> {
            bookingService.updateBooking(userId, bookingId, approved);
        });
    }

    @Test
    public void testUpdateBookingAlreadyApproved() {
        Long userId = 1L;
        Long bookingId = 2L;
        Boolean approved = true;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        Item item = new Item();
        item.setOwner(new User(userId, "owner", "owner@mail.ru"));
        booking.setItem(item);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));

        assertThrows(IllegalStateException.class, () -> {
            bookingService.updateBooking(userId, bookingId, approved);
        });
    }

    @Test
    public void testGetBooking() {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusDays(1), item2, booker, BookingStatus.APPROVED);
        booking.setItem(new Item(1L, "item2", "description2", true, new User(userId, "owner", "owner@mail.ru"), null, null));

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "owner", "owner@mail.ru")));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(new Item()));
        when(bookingMapper.bookingDtoFromBooking(booking)).thenReturn(bookingDto2);

        BookingDto result = bookingService.getBooking(userId, bookingId);

        assertEquals(bookingDto2, result);
    }

    @Test
    public void testGetBookingUserNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(userId, bookingId);
        });
    }

    @Test
    public void testGetBookingNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(userId, bookingId);
        });
    }

    @Test
    public void testGetBookingItemNotFound() {
        Long userId = 1L;
        Long bookingId = 2L;
        Booking booking = new Booking();
        booking.setItem(new Item());

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getBooking(userId, bookingId);
        });
    }

    @Test
    public void testGetBookingAccessDenied() {
        Long userId = 1L;
        Long bookingId = 2L;
        Long otherUserId = 2L;
        Booking booking = new Booking();
        User booker = new User(otherUserId, "booker", "booker@mail.ru");
        User owner = new User(otherUserId, "otherOwner", "other@mail.ru");
        Item item = new Item();
        item.setOwner(owner);
        booking.setItem(item);
        booking.setBooker(booker);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "user", "user@mail.ru")));
        when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        when(itemRepository.findById(booking.getItem().getId())).thenReturn(Optional.of(item));

        assertThrows(AccessDeniedException.class, () -> {
            bookingService.getBooking(userId, bookingId);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "ALL, findAllByBooker",
            "CURRENT, findAllByBookerAndStartBeforeAndEndAfter",
            "PAST, findAllByBooker",
            "FUTURE, findAllByBooker",
            "WAITING, findAllByBookerAndStatus",
            "REJECTED, findAllByBookerAndStatus"
    })
    public void testGetAllBooking(String state, String expectedMethod) {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        User user = new User(userId, "booker", "booker@mail.ru");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = Arrays.asList(
                new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED),
                new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, BookingStatus.PAST)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(bookingMapper.bookingDtoFromBooking(any(Booking.class)))
                .thenReturn(new BookingDto(), new BookingDto());

        switch (expectedMethod) {
            case "findAllByBooker":
                when(bookingRepository.findAllByBooker(any(User.class), eq(pageable))).thenReturn(bookings);
                break;
            case "findAllByBookerAndStartBeforeAndEndAfter":
                when(bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                        .thenReturn(bookings);
                break;
            case "findAllByBookerAndStatus":
                BookingStatus status = state.equalsIgnoreCase("WAITING") ? BookingStatus.WAITING : BookingStatus.REJECTED;
                when(bookingRepository.findAllByBookerAndStatus(any(User.class), eq(status), eq(pageable)))
                        .thenReturn(bookings);
                break;
        }

        List<Booking> filteredBookings = bookings;
        if (state.equalsIgnoreCase("PAST")) {
            filteredBookings = bookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .collect(Collectors.toList());
        } else if (state.equalsIgnoreCase("FUTURE")) {
            filteredBookings = bookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .collect(Collectors.toList());
        }

        List<BookingDto> result = bookingService.getAllBooking(userId, state, pageable);

        assertEquals(filteredBookings.size(), result.size());
    }

    @Test
    public void testGetAllBookingUserNotFound() {
        Long userId = 1L;
        String state = "ALL";
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBooking(userId, state, pageable);
        });
    }

    @Test
    public void testGetAllBookingInvalidState() {
        Long userId = 1L;
        String state = "INVALID";
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "user", "user@mail.ru")));

        assertThrows(ValidationException.class, () -> {
            bookingService.getAllBooking(userId, state, pageable);
        });
    }

    @ParameterizedTest
    @CsvSource({
            "ALL, findAllByItemOwner",
            "CURRENT, findAllByItemOwnerAndStartBeforeAndEndAfter",
            "PAST, findAllByItemOwnerAndEndBefore",
            "FUTURE, findAllByItemOwnerAndStartAfter",
            "WAITING, findAllByItemOwnerAndStatus",
            "REJECTED, findAllByItemOwnerAndStatus"
    })
    public void testGetAllBookingsByOwner(String state, String expectedMethod) {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        User owner = new User(userId, "owner", "owner@mail.ru");
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = Arrays.asList(
                new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1), null, null, BookingStatus.APPROVED),
                new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1), null, null, BookingStatus.PAST)
        );

        when(userRepository.findById(userId)).thenReturn(Optional.of(owner));
        when(bookingMapper.bookingDtoFromBooking(any(Booking.class)))
                .thenReturn(new BookingDto(), new BookingDto());

        switch (expectedMethod) {
            case "findAllByItemOwner":
                when(bookingRepository.findAllByItemOwner(any(User.class), eq(pageable))).thenReturn(bookings);
                break;
            case "findAllByItemOwnerAndStartBeforeAndEndAfter":
                when(bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(any(User.class), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                        .thenReturn(bookings);
                break;
            case "findAllByItemOwnerAndEndBefore":
                when(bookingRepository.findAllByItemOwnerAndEndBefore(any(User.class), any(LocalDateTime.class), eq(pageable)))
                        .thenReturn(bookings);
                break;
            case "findAllByItemOwnerAndStartAfter":
                when(bookingRepository.findAllByItemOwnerAndStartAfter(any(User.class), any(LocalDateTime.class), eq(pageable)))
                        .thenReturn(bookings);
                break;
            case "findAllByItemOwnerAndStatus":
                BookingStatus status = state.equalsIgnoreCase("WAITING") ? BookingStatus.WAITING : BookingStatus.REJECTED;
                when(bookingRepository.findAllByItemOwnerAndStatus(any(User.class), eq(status), eq(pageable)))
                        .thenReturn(bookings);
                break;
        }

        List<BookingDto> result = bookingService.getAllBookingsByOwner(userId, state, pageable);

        assertEquals(2, result.size());
    }

    @Test
    public void testGetAllBookingsByOwnerUserNotFound() {
        Long userId = 1L;
        String state = "ALL";
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            bookingService.getAllBookingsByOwner(userId, state, pageable);
        });
    }

    @Test
    public void testGetAllBookingsByOwnerInvalidState() {
        Long userId = 1L;
        String state = "INVALID";
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(userId)).thenReturn(Optional.of(new User(userId, "user", "user@mail.ru")));

        assertThrows(ValidationException.class, () -> {
            bookingService.getAllBookingsByOwner(userId, state, pageable);
        });
    }
}
