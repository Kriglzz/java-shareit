package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    ResponseEntity<Object> createItem(@RequestHeader(name = "X-Sharer-User-Id") @NotNull Long userId,
                                      @Valid @RequestBody ItemDto itemDto) {
        return itemClient.addItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(name = "X-Sharer-User-Id") @NotNull Long userId,
                                             @PathVariable @NotNull Long itemId,
                                             @RequestBody @NotNull ItemDto itemDto) {
        return itemClient.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(name = "X-Sharer-User-Id") @NotNull Long userId,
                                              @PathVariable @NotNull Long itemId) {
        return itemClient.getItemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                                  @RequestParam(defaultValue = "0") @Min(0) int from,
                                                  @RequestParam(defaultValue = "10") @Min(1) int size) {
        return itemClient.getAllUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam String text,
                                         @RequestParam(defaultValue = "0") @Min(0) int from,
                                         @RequestParam(defaultValue = "10") @Min(1) int size) {
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(name = "X-Sharer-User-Id") @NotNull Long userId,
                                             @PathVariable @NotNull Long itemId,
                                             @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addComment(itemId, userId, commentDto);
    }

}
