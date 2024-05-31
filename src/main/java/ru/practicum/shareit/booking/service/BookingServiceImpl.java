package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;


    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {

        if (!bookingDto.validateDates()) {
            throw new IllegalStateException("Ошибка даты");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id " + bookingDto.getItemId() + " не найден"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new NotFoundException("Владелец не может забронировать свой же предмет");
        } else if (!item.getAvailable()) {
            throw new ValidationException("Предмет с id " + item.getId() + " не доступен");
        }

        Booking booking = bookingMapper.bookingFromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.bookingDtoFromBooking(savedBooking);
    }

    private void checkAvailable(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректноые данные. Проверьте статус \"available\"");
        }
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {

        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id \"" + bookingId + "\" не найдено"));

        itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Вещь с id \"" + userId + "\" не найдена"));

        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new AccessDeniedException("У вас нет прав на изменение этого бронирования");
        }

        if (approved && booking.getStatus() == BookingStatus.APPROVED) {
            throw new IllegalStateException("Бронирование уже подтверждено");
        }

        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        bookingRepository.save(booking);

        return bookingMapper.bookingDtoFromBooking(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {

        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id \"" + bookingId + "\" не найдено"));

        itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Вещь с id \"" + userId + "\" не найдена"));

        if (!userHasAccessToBooking(userId, booking)) {
            throw new AccessDeniedException("У вас нет доступа к этому бронированию");
        }

        return bookingMapper.bookingDtoFromBooking(booking);
    }

    private boolean userHasAccessToBooking(Long userId, Booking booking) {
        return userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId());
    }

    @Override
    public List<BookingDto> getAllBooking(Long userId, String state, Pageable pageable) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findAllByBooker(user, pageable);
        } else {

            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, now, now,
                            pageable);
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByBooker(user,
                            pageable);
                    bookings.removeIf(booking -> booking.getEnd().isAfter(now));
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByBooker(user,
                            pageable);
                    bookings.removeIf(booking -> booking.getStart().isBefore(now));
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.WAITING,
                            pageable);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.REJECTED,
                            pageable);
                    break;
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        }

        return bookings.stream()
                .map(bookingMapper::bookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long userId, String state, Pageable pageable) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();
        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findAllByItemOwner(owner, pageable);
        } else {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(owner, now, now,
                            pageable);
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByItemOwnerAndEndBefore(owner, now, pageable);
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByItemOwnerAndStartAfter(owner, now, pageable);
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.WAITING,
                            pageable);
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.REJECTED,
                            pageable);
                    break;
                default:
                    throw new ValidationException("Unknown state: " + state);
            }
        }

        return bookings.stream()
                .map(bookingMapper::bookingDtoFromBooking)
                .collect(Collectors.toList());
    }

}
