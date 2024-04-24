package ru.practicum.shareit;

import org.springframework.http.HttpStatus;

public class ResponseWrapper<T> {

    private final T data;
    private final HttpStatus status;

    public ResponseWrapper(T data, HttpStatus status) {
        this.data = data;
        this.status = status;
    }

    public T getData() {
        return data;
    }

    public HttpStatus getStatus() {
        return status;
    }
}

