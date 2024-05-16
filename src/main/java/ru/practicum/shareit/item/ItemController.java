package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
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
    public ResponseEntity<ItemDto> getItemById(@PathVariable long itemId) {
        ItemDto item = itemService.getItemById(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        itemService.deleteItemById(itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        List<ItemDto> items = itemService.getAllUserItems(userId, sort);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        List<ItemDto> searched = itemService.search(text);
        return new ResponseEntity<>(searched, HttpStatus.OK);
    }
}
