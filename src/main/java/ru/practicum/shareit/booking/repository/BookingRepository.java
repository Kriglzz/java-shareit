package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBooker(User booker, Pageable page);

    /**
     * CURRENT, WAITING, REJECTED for booker
     */
    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Pageable page);

    List<Booking> findAllByItemOwner(User itemOwner, Pageable page);

    /**
     * CURRENT, WAITING, REJECTED for owner
     */
    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Pageable page);

    /**
     * LastBooking
     */
    Optional<Booking> findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(Long itemId, LocalDateTime end,
                                                                                  BookingStatus status);

    /**
     * NextBooking
     */
    Optional<Booking> findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(Long itemId, LocalDateTime start,
                                                                                BookingStatus status);

    /**
     * CURRENT for owner
     */
    List<Booking> findAllByItemOwnerAndStartBeforeAndEndAfter(User itemOwner, LocalDateTime now,
                                                              LocalDateTime andNow, Pageable page);

    /**
     * CURRENT for booker
     */
    List<Booking> findAllByBookerAndStartBeforeAndEndAfter(User booker, LocalDateTime now,
                                                           LocalDateTime andNow, Pageable page);

    /**
     * PAST for owner
     */
    List<Booking> findAllByItemOwnerAndEndBefore(User itemOwner, LocalDateTime end, Pageable page);

    /**
     * FUTURE for owner
     */
    List<Booking> findAllByItemOwnerAndStartAfter(User itemOwner, LocalDateTime start, Pageable page);

    List<Booking> findByItemAndBookerAndStatus(Item item, User booker, BookingStatus status);
}
