package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;


    @PostMapping
    public BookingDto createBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                    @RequestBody @Valid BookingDto bookingDto) {
        return bookingService.createBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(value = "approved") Boolean approved) {
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                 @PathVariable Long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getOwnerBookings(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                             @RequestParam(value = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllBooking(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                  @RequestParam(value = "state",
                                                          defaultValue = "ALL",
                                                          required = false) String state) {
        return bookingService.getAllBookingsByOwner(userId, state);
    }
}
