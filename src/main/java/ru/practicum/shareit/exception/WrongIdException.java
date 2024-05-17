package ru.practicum.shareit.exception;

public class WrongIdException extends RuntimeException {
    public WrongIdException(String message) {
        super(message);
    }
}
