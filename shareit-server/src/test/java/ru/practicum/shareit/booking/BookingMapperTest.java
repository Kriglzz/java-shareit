package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BookingMapperTest {

    private final BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);

    @Test
    public void testBookingFromBookingDto() {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(1L);
        bookingDto.setBookerId(2L);

        Booking booking = bookingMapper.bookingFromBookingDto(bookingDto);

        assertEquals(bookingDto.getItemId(), booking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), booking.getBooker().getId());
    }

    @Test
    public void testBookingDtoFromBooking() {

        Booking booking = new Booking();
        booking.setItem(new Item(2L, "item2", "descrirption2",
                true, null, null, null));
        booking.setBooker(new User(1L, "name", "mail@mail.ru"));

        BookingDto bookingDto = bookingMapper.bookingDtoFromBooking(booking);

        assertEquals(booking.getItem().getId(), bookingDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }
}
