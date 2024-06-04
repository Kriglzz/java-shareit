package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static org.springframework.data.domain.Sort.Direction.DESC;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
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
    @Valid
    public ResponseEntity<List<BookingDto>> getOwnerBookings(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                             @RequestParam(defaultValue = "ALL") String state,
                                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                             @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(0, from / size), size, Sort.by(DESC, "start"));
        List<BookingDto> bookings = bookingService.getAllBooking(userId, state, pageable);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }

    @GetMapping("/owner")
    @Valid
    public ResponseEntity<List<BookingDto>> getAllBookingsByOwner(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                                  @RequestParam(defaultValue = "ALL") String state,
                                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(Math.max(0, from / size), size, Sort.by(DESC, "start"));
        List<BookingDto> bookings = bookingService.getAllBookingsByOwner(userId, state, pageable);
        return new ResponseEntity<>(bookings, HttpStatus.OK);
    }
}
