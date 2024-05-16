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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDate;
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

    private final ItemMapper itemMapper;

    private final UserMapper userMapper;

    @Override
    public BookingDto createBooking(Long userId, Booking booking) {

        Item item = itemRepository.findById(booking.getItem().getId()).orElseThrow(() ->
                new NotFoundException("Предмет с таким id не найден"));
        ItemDto itemDto = itemMapper.itemDtoFromItem(item);
        checkAvailable(itemDto);

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));
        UserDto userDto = userMapper.userDtoFromUser(user);

        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);

        return bookingMapper.bookingDtoFromBooking(booking);
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

            return bookingMapper.bookingDtoFromBooking(booking);
        }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException("Бронирование с id \"" + bookingId + "\" не найдено"));

        // Проверяем, имеет ли пользователь доступ к этому бронированию
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

        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        if (state == null || state.equalsIgnoreCase("ALL")) {
            bookings = bookingRepository.findAllByBooker(user, Sort.by(Sort.Direction.DESC, "createdAt"));
        } else {

            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    bookings.removeIf(booking -> booking.getEnd().toLocalDate().isAfter(LocalDate.now()));
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    bookings.removeIf(booking -> booking.getStart().toLocalDate().isBefore(LocalDate.now()));
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.WAITING,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByBookerAndStatus(user, BookingStatus.REJECTED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
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
            bookings = bookingRepository.findAllByItemOwner(owner, Sort.by(Sort.Direction.DESC, "createdAt"));
        } else {
            switch (state.toUpperCase()) {
                case "CURRENT":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                case "PAST":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    bookings.removeIf(booking -> booking.getEnd().toLocalDate().isAfter(LocalDate.now()));
                    break;
                case "FUTURE":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.APPROVED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    bookings.removeIf(booking -> booking.getStart().toLocalDate().isBefore(LocalDate.now()));
                    break;
                case "WAITING":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.WAITING,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
                    break;
                case "REJECTED":
                    bookings = bookingRepository.findAllByItemOwnerAndStatus(owner, BookingStatus.REJECTED,
                            Sort.by(Sort.Direction.DESC, "createdAt"));
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
