package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.Comment;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        checkAvailable(itemDto);
        UserDto owner = userService.getUserById(userId);
        Item item = itemMapper.itemFromItemDto(itemDto);
        item.setOwner(userMapper.userFromUserDto(owner));
        Item savedItem = itemRepository.save(item);
        return itemMapper.itemDtoFromItem(savedItem);
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        checkXShare(item.getId(), userId);
        updateItemFields(item, itemDto);
        Item updatedItem = itemRepository.save(item);
        return itemMapper.itemDtoFromItem(updatedItem);
    }

    private void updateItemFields(Item item, ItemDto itemDto) {
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
    }

    @Override
    public ItemDto getItemById(Long userId, long itemId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        ItemDto itemDto = itemMapper.itemDtoFromItem(item);
        LocalDateTime localDateTime = LocalDateTime.now();
        if (Objects.equals(item.getOwner().getId(), userId)) {
            Optional<Booking> lastBookingOpt = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                    item.getId(), localDateTime, BookingStatus.REJECTED);
            Optional<Booking> nextBookingOpt = bookingRepository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                    item.getId(), localDateTime, BookingStatus.REJECTED);

            BookingDto lastBookingDto = lastBookingOpt.map(bookingMapper::bookingDtoFromBooking).orElse(null);
            BookingDto nextBookingDto = nextBookingOpt.map(bookingMapper::bookingDtoFromBooking).orElse(null);

            itemDto.setLastBooking(lastBookingDto);
            itemDto.setNextBooking(nextBookingDto);
        } else {
            itemDto.setLastBooking(null);
            itemDto.setNextBooking(null);
        }
        List<CommentDto> commentDtos = commentRepository.findAllByItem(item).stream()
                .map(commentMapper::commentDtoFromComment)
                .collect(Collectors.toList());
        itemDto.setComments(commentDtos);
        return itemDto;
    }

    @Override
    public void deleteItemById(Long itemId) {
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> getAllItems() {
        List<Item> allItems = itemRepository.findAll();
        return allItems.stream().map(itemMapper::itemDtoFromItem).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getAllUserItems(Long userId, Sort sort) {
        List<Item> userItems = itemRepository.findAllByOwnerId(userId, sort);
        LocalDateTime localDateTime = LocalDateTime.now();

        return userItems.stream().map(item -> {
            ItemDto itemDto = itemMapper.itemDtoFromItem(item);

            List<CommentDto> commentDtos = commentRepository.findAllByItem(item).stream()
                    .map(commentMapper::commentDtoFromComment)
                    .collect(Collectors.toList());
            itemDto.setComments(commentDtos);

            if (Objects.equals(item.getOwner().getId(), userId)) {
                Optional<Booking> lastBookingOpt = bookingRepository.findFirstByItemIdAndStartBeforeAndStatusIsNotOrderByEndDesc(
                        item.getId(), localDateTime, BookingStatus.REJECTED);
                Optional<Booking> nextBookingOpt = bookingRepository.findFirstByItemIdAndStartAfterAndStatusIsNotOrderByEndAsc(
                        item.getId(), localDateTime, BookingStatus.REJECTED);

                BookingDto lastBookingDto = lastBookingOpt.map(bookingMapper::bookingDtoFromBooking).orElse(null);
                BookingDto nextBookingDto = nextBookingOpt.map(bookingMapper::bookingDtoFromBooking).orElse(null);

                itemDto.setLastBooking(lastBookingDto);
                itemDto.setNextBooking(nextBookingDto);
            } else {
                itemDto.setLastBooking(null);
                itemDto.setNextBooking(null);
            }
            return itemDto;
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.trim().isEmpty()) {
            return List.of();
        }
        String searchText = text.toLowerCase();
        List<Item> searchResults = itemRepository.findByNameOrDescriptionContainingIgnoreCase(searchText);

        return searchResults.stream()
                .filter(Item::getAvailable) // Фильтруем по доступности
                .map(itemMapper::itemDtoFromItem)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));

        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));

        List<Booking> bookings = bookingRepository.findByItemAndBookerAndStatus(item, user, BookingStatus.APPROVED);
        if (bookings.isEmpty()) {
            throw new ValidationException("Пользователь не может оставить комментарий для предмета без подтвержденного бронирования");
        }

        boolean hasPastBooking = bookings.stream().anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()));
        if (!hasPastBooking) {
            throw new ValidationException("Вы не можете оставлять отзыв к этой вещи до окончания бронирования");
        }

        Comment newComment = new Comment();
        newComment.setText(commentDto.getText());
        newComment.setItem(item);
        newComment.setUser(user);
        newComment.setCreated(LocalDateTime.now());

        commentRepository.save(newComment);

        return commentMapper.commentDtoFromComment(newComment);
    }


    private void checkAvailable(ItemDto item) {
        if (item.getAvailable() == null) {
            throw new ValidationException("Некорректноые данные. Проверьте статус \"available\"");
        }
    }

    private void checkXShare(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException("Предмет с id \"" + itemId + "\" не найден"));
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException("Предмет с id \"" + itemId +
                    "\" не принадлежит пользователю с id \"" + userId + "\"");
        }
    }
}
