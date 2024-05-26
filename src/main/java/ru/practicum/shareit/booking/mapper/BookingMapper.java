package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {
    @Mapping(source = "itemId", target = "item.id")
    @Mapping(source = "bookerId", target = "booker.id")
    Booking bookingFromBookingDto(BookingDto bookingDto);

    @Mapping(source = "item.id", target = "itemId")
    @Mapping(source = "booker.id", target = "bookerId")
    BookingDto bookingDtoFromBooking(Booking booking);
}
