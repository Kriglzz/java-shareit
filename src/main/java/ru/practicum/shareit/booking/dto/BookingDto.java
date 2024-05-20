package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;
    private Long itemId;
    private Long bookerId;
    private LocalDateTime start;
    private LocalDateTime end;
    private ItemDto item;
    private UserDto booker;
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    public Boolean validateDates() {
        if (start == null || end == null || start.isAfter(end) ||
                end.isBefore(start) || start == end || start.equals(end) || start.isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }
}
