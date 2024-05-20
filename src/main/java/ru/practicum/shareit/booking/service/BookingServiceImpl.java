package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.WrongIdException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;


    @Override
    public BookingDto createBooking(Long userId, BookingDto bookingDto) {
        System.out.println("BookingDto Start: " + bookingDto.getStart());
        System.out.println("BookingDto End: " + bookingDto.getEnd());

        if (!bookingDto.validateDates()) {
            throw new IllegalStateException("Ошибка даты");
        }

        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() ->
                new NotFoundException("Предмет с id " + bookingDto.getItemId() + " не найден"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        if (Objects.equals(item.getOwner().getId(), user.getId())) {
            throw new ValidationException("Владелец не может забронировать свой же предмет");
        } else if (!item.getAvailable()) {
            throw new ValidationException("Предмет с id " + item.getId() + " не доступен");
        }

        Booking booking = bookingMapper.bookingFromBookingDto(bookingDto);
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);

        Booking savedBooking = bookingRepository.save(booking);
        System.out.println("Start: " + booking.getStart());
        System.out.println("End: " + booking.getEnd());
        return bookingMapper.bookingDtoFromBooking(savedBooking);
    }

    private void checkAvailable(ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Некорректноые данные. Проверьте статус \"available\"");
        }
    }

    @Override
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {

            Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                    new NotFoundException("Бронирование с id \"" + bookingId + "\" не найдено"));

            if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
                throw new AccessDeniedException("У вас нет прав на изменение этого бронирования");
            }

            if (approved && booking.getStatus() == BookingStatus.APPROVED) {
                throw new IllegalStateException("Бронирование уже подтверждено");
            }

            booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
            bookingRepository.save(booking);
        System.out.println("Start: " + booking.getStart());
        System.out.println("End: " + booking.getEnd());

            return bookingMapper.bookingDtoFromBooking(booking);
        }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id \"" + bookingId + "\" не найдено"));

        if (!userHasAccessToBooking(userId, booking)) {
            throw new AccessDeniedException("У вас нет доступа к этому бронированию");
        }

        return bookingMapper.bookingDtoFromBooking(booking);
    }

    private boolean userHasAccessToBooking(Long userId, Booking booking) {
        return userId.equals(booking.getBooker().getId()) || userId.equals(booking.getItem().getOwner().getId());
    }

    @Override
    public List<BookingDto> getAllBooking(Long userId, String state) {
        List<Booking> bookings;
        LocalDateTime now = LocalDateTime.now();

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findAllByBooker(user, Sort.by(Sort.Direction.DESC, "start"));
        } else {

            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.CURRENT,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByBooker(user,
                            Sort.by(Sort.Direction.DESC, "start"));
                    bookings.removeIf(booking -> booking.getEnd().isAfter(now));
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByBooker(user,
                            Sort.by(Sort.Direction.DESC, "start"));
                    bookings.removeIf(booking -> booking.getStart().isBefore(now));
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.WAITING,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.REJECTED,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                default:
                    throw new IllegalArgumentException("Некорректное значение параметра state: " + state);
            }
        }

        return bookings.stream()
                .map(bookingMapper::bookingDtoFromBooking)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllBookingsByOwner(Long userId, String state) {
        User owner = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        List<Booking> bookings;

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findAllByItemOwner(owner, Sort.by(Sort.Direction.DESC, "start"));
        } else {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.CURRENT,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByItemOwner(owner,
                            Sort.by(Sort.Direction.DESC, "start"));
                    bookings.removeIf(booking -> booking.getEnd().toLocalDate().isAfter(LocalDate.now()));
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByItemOwner(owner,
                            Sort.by(Sort.Direction.DESC, "start"));
                    bookings.removeIf(booking -> booking.getStart().toLocalDate().isBefore(LocalDate.now()));
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.WAITING,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.REJECTED,
                            Sort.by(Sort.Direction.DESC, "start"));
                    break;
                default:
                    throw new IllegalArgumentException("Некорректное значение параметра state: " + state);
            }
        }

        return bookings.stream()
                .map(bookingMapper::bookingDtoFromBooking)
                .collect(Collectors.toList());
    }

}
