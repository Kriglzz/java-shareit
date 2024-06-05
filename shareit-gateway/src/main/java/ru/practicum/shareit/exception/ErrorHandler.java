package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.request.controller.ItemRequestController;
import java.util.Map;

@RestControllerAdvice(assignableTypes = {
        UserController.class,
        ItemController.class,
        BookingController.class,
        ItemRequestController.class
})
public class ErrorHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleIllegalArgumentException(final IllegalArgumentException e) {
        return Map.of("error", e.getMessage());
    }

}
