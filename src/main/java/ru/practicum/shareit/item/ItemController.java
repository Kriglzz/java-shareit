package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemRepository itemRepository;

    /*@PostMapping
    public ResponseEntity<ResponseWrapper<ItemDto>> addItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                            @RequestBody @Validated ItemDto itemDto) {
        ItemDto item = itemRepository.addItem(userId, itemDto);
        return new ResponseEntity<>(new ResponseWrapper<>(item), HttpStatus.CREATED);
    }*/

    @PostMapping
    public ResponseEntity<ItemDto> addItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                           @RequestBody @Validated ItemDto itemDto) {
        ItemDto item = itemRepository.addItem(userId, itemDto);
        return new ResponseEntity<>(item, HttpStatus.CREATED);
    }

    /*@PatchMapping("/{itemId}")
    public ResponseEntity<ResponseWrapper<ItemDto>> updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                                               @PathVariable("itemId") long itemId,
                                                               @RequestBody ItemDto itemDto) {
        ItemDto item = itemRepository.updateItem(itemId, userId, itemDto);
        return new ResponseEntity<>(new ResponseWrapper<>(item), HttpStatus.OK);
    }*/

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                              @PathVariable("itemId") long itemId,
                                              @RequestBody ItemDto itemDto) {
        ItemDto item = itemRepository.updateItem(itemId, userId, itemDto);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    /*@GetMapping("/{itemId}")
    public ResponseEntity<ResponseWrapper<ItemDto>> getItemById(@PathVariable long itemId) {
        ItemDto item = itemRepository.getItemById(itemId);
        return new ResponseEntity<>(new ResponseWrapper<>(item), HttpStatus.FOUND);
    }*/

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItemById(@PathVariable long itemId) {
        ItemDto item = itemRepository.getItemById(itemId);
        return new ResponseEntity<>(item, HttpStatus.OK);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        itemRepository.deleteItemById(itemId);
    }

    /*@GetMapping
    public ResponseEntity<ResponseWrapper<List<ItemDto>>> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        List<ItemDto> items = itemRepository.getAllUserItems(userId);
        return new ResponseEntity<>(new ResponseWrapper<>(items), HttpStatus.FOUND);
    }*/

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        List<ItemDto> items = itemRepository.getAllUserItems(userId);
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam String text) {
        List<ItemDto> searched = itemRepository.search(text);
        return new ResponseEntity<>(searched, HttpStatus.OK);
    }
}
