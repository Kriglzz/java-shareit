package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Service
public interface BookingService {
    BookingDto createBooking(Long userId, BookingDto bookingDto);

    BookingDto updateBooking(Long userId, Long bookingId, Boolean approved);

    BookingDto getBooking(Long userId, Long bookingId);

    List<BookingDto> getAllBooking(Long userId, String state);

    List<BookingDto> getAllBookingsByOwner(Long userId, String state);
}
