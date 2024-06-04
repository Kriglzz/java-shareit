package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.ErrorHandler;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({UserController.class, ItemController.class, BookingController.class, ItemRequestController.class, ErrorHandler.class})
public class ErrorHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserController userController;

    @MockBean
    private ItemController itemController;

    @MockBean
    private BookingController bookingController;

    @MockBean
    private ItemRequestController itemRequestController;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private BookingRepository bookingRepository;

    @MockBean
    private ItemRepository itemRepository;
    @MockBean
    private BookingService bookingService;


    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();
    }

    @Test
    public void handleNotFoundException() throws Exception {
        doThrow(new NotFoundException("Not Found Exception")).when(userController).getUserById(1L);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void handleValidationException() throws Exception {
        setUp();
        BookingDto invalidBookingDto = new BookingDto();
        invalidBookingDto.setItemId(null);

        doThrow(new ValidationException("Validation Exception")).when(bookingService).createBooking(1L, invalidBookingDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"itemId\":null,\"start\":\"2024-06-01T12:00:00\",\"end\":\"2024-06-02T12:00:00\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void handleIllegalStateException() throws Exception {
        setUp();
        Long userId = 1L;
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.parse("2023-06-01T00:00:00"));
        bookingDto.setEnd(LocalDateTime.parse("2023-06-02T00:00:00"));

        doThrow(new IllegalStateException("Ошибка даты")).when(bookingService).createBooking(any(Long.class), any(BookingDto.class));

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\":1,\"start\":\"2023-06-01T00:00:00\",\"end\":\"2023-06-02T00:00:00\"}"))
                .andExpect(status().is(HttpStatus.BAD_REQUEST.value()));
    }
}
