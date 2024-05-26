package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
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
    public ResponseEntity<BookingDto> createBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                    @RequestBody @Valid BookingDto bookingDto) {
        BookingDto booking = bookingService.createBooking(userId, bookingDto);
        return new ResponseEntity<>(booking, HttpStatus.CREATED);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                    @PathVariable Long bookingId,
                                                    @RequestParam(value = "approved") Boolean approved) {
        BookingDto bookingDto = bookingService.updateBooking(userId, bookingId, approved);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                 @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.getBooking(userId, bookingId);
        return new ResponseEntity<>(bookingDto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                             @RequestParam(value = "state",
                                                                     defaultValue = "ALL") String state) {
        List<BookingDto> bookings = bookingService.getAllBooking(userId, state);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/owner")
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                                  @RequestParam(value = "state",
                                                                          defaultValue = "ALL",
                                                                          required = false) String state) {
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(userId, state);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
