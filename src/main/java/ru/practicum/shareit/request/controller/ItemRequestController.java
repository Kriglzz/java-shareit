package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import org.springframework.data.domain.Pageable;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService requestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> create(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                        @RequestBody @Validated ItemRequestDto itemRequestDto) {
        ItemRequestDto itemRequest = requestService.createItemRequest(userId, itemRequestDto);
        return new ResponseEntity<>(itemRequest, HttpStatus.CREATED);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<ItemRequestDto> getById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                  @PathVariable("requestId") long requestId) {
        ItemRequestDto itemRequest = requestService.getById(userId, requestId);
        return new ResponseEntity<>(itemRequest, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ItemRequestDto>> getByOwner(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        List <ItemRequestDto> itemRequestList = requestService.getByOwner(userId);
        return new ResponseEntity<>(itemRequestList, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ItemRequestDto>> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                               @PositiveOrZero @RequestParam(required = false,
                                                                       defaultValue = "0") Integer from,
                                                               @Positive @RequestParam(required = false,
                                                                       defaultValue = "10") Integer size) {
        int page = from / size;
        List<ItemRequestDto> itemRequestList = requestService.getAllRequests(userId, PageRequest.of(page, size));
        return new ResponseEntity<>(itemRequestList, HttpStatus.OK);
    }
}
