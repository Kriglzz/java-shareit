package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated ItemDto itemDto) {
        ItemDto item = itemService.addItem(userId, itemDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                              @PathVariable("itemId") long itemId,
                                              @RequestBody ItemDto itemDto) {
        ItemDto item = itemService.updateItem(itemId, userId, itemDto);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                               @PathVariable long itemId) {
        ItemDto item = itemService.getItemById(userId, itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable long itemId) {
        itemService.deleteItemById(itemId);
    }

    @GetMapping
    @Valid
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                         @PositiveOrZero @RequestParam(defaultValue = "0") int page,
                                                         @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDto> items = itemService.getAllUserItems(userId, pageable);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    @Valid
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int page,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ItemDto> searched = itemService.search(text, pageable);
        return new ResponseEntity<>(searched, HttpStatus.OK);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @RequestBody @Valid CommentDto commentDto) {
        CommentDto comment = itemService.addComment(itemId, userId, commentDto);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }
}
