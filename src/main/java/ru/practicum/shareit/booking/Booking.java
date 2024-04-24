package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private Long booker;
    private String status;

}
