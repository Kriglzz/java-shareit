package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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

    @PostMapping
    public ItemDto addItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                           @RequestBody @Validated ItemDto itemDto) {
        return itemRepository.addItem(userId, itemDto);
    }

    /* @PatchMapping("/{itemId}")
     public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                           @RequestBody ItemDto itemDto) {
         return itemRepository.updateItem(userId, itemDto);
     }*/
    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                              @PathVariable("itemId") long itemId,
                              @RequestBody ItemDto itemDto) {
        return itemRepository.updateItem(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId) {
        return itemRepository.getItemById(itemId);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItemById(@PathVariable Long itemId) {
        itemRepository.deleteItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getAllUserItems(@RequestHeader(name = "X-Sharer-User-Id") long userId) {
        return itemRepository.getAllUserItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemRepository.search(text);
    }
}
