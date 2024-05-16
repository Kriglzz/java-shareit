package ru.practicum.shareit.booking;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid Booking booking) {
        return bookingService.createBooking(userId, booking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(value = "approved") Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllBooking(userId, state);
    }

    @GetMapping
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "state",
                                                          defaultValue = "ALL",
                                                          required = false) String state) {
        return bookingService.getAllBookingsByOwner(userId, state);
    }
}
