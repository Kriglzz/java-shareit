package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Sort sort);
    /**
     * CURRENT, WAITING, REJECTED for booker
     */
    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);
    /**
     * CURRENT, WAITING, REJECTED for owner
     */
    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    /**
     * LastBooking
     */
    Booking findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(Long itemId, LocalDateTime end,
                                                                        BookingStatus status);
    /**
     * NextBooking
     */
    Booking findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(Long itemId, LocalDateTime start,
                                                                      BookingStatus status);
    /**
     * CURRENT for owner
     */
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User itemOwner, LocalDateTime now,
                                                              LocalDateTime andNow, Sort sort);
    /**
     * CURRENT for booker
     */
    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime now,
                                                           LocalDateTime andNow, Sort sort);
    /**
     * PAST for owner
     */
    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime end, Sort sort);
    /**
     * FUTURE for owner
     */
    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime start, Sort sort);

    List<Booking> findByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                                LocalDateTime now,
                                                                                BookingStatus status);
}
