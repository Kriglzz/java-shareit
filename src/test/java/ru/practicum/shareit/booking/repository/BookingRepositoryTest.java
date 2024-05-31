package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker;
    private User owner;
    private Item item1;
    private Item item2;
    private Booking booking1;
    private Booking booking2;

    private Pageable pageable;

    @BeforeEach
    void setUp() {
        booker = userRepository.save(new User(1L, "booker", "booker@mail.ru"));
        owner = userRepository.save(new User(2L, "owner", "owner@mail.ru"));
        item1 = itemRepository.save(new Item(1L, "item1", "description1",
                true, owner, null, null));
        item2 = itemRepository.save(new Item(2L, "item2", "description2",
                true, owner, null, null));

        booking1 = new Booking(1L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item1, booker, BookingStatus.APPROVED);
        booking2 = new Booking(2L, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item2, booker, BookingStatus.APPROVED);
        bookingRepository.saveAll(List.of(booking1, booking2));
        pageable = PageRequest.of(0, 10);
    }

    @Test
    void testFindAllByBooker() {
        List<Booking> bookings = bookingRepository.findAllByBooker(booker, pageable);
        assertThat(bookings).containsExactly(booking1, booking2);
    }

    @Test
    void testFindAllByItemOwnerAndStatus() {
        List<Booking> bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.APPROVED, pageable);
        assertThat(bookings).containsExactly(booking1, booking2);
    }

}
