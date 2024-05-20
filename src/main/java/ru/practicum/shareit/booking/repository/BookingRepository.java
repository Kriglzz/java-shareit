package ru.practicum.shareit.booking.repository;

import ch.qos.logback.core.status.Status;
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

    List<Booking> findAllByBookerAndStatus(User booker, BookingStatus status, Sort sort);

    List<Booking> findAllByItemOwner(User itemOwner, Sort sort);

    List<Booking> findAllByItemOwnerAndStatus(User itemOwner, BookingStatus status, Sort sort);

    List<Booking> findAllByItemIn(List<Item> items, Sort sort);

    Booking findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(Long itemId, LocalDateTime end,
                                                                        BookingStatus status);

    Booking findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(Long itemId, LocalDateTime start,
                                                                      BookingStatus status);

    Optional<Booking> findFirstByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(Long bookerId, Long itemId,
                                                                                LocalDateTime now,
                                                                                BookingStatus status);
}
