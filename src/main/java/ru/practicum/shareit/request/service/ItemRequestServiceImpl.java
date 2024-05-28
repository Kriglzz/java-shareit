package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;

    private final UserRepository userRepository;

    @Override
    public ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto) {
        User requester = checkUser(userId);
        itemRequestDto.setId(userId);
        ItemRequest itemRequest = itemRequestMapper.itemRequestFromItemRequestDto(itemRequestDto);
        itemRequest.setRequester(requester);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        return itemRequestMapper.itemRequestDtoFromItemRequest(savedItemRequest);
    }

    @Override
    public ItemRequestDto getById(Long userId, Long requestId) {
        checkUser(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос с id \"" + requestId + "\" не найден"));
        return itemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getByOwner(Long userId) {
        checkUser(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(userId);
        return itemRequests.stream()
                .map(itemRequestMapper::itemRequestDtoFromItemRequest)
                .collect(Collectors.toList());
    }

    public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageRequest) {
        checkUser(userId);
        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest);
        return itemRequestsPage.stream()
                .map(itemRequestMapper::itemRequestDtoFromItemRequest)
                .collect(Collectors.toList());
    }

    /*public List<ItemRequestDto> getAllRequests(Long userId, Pageable pageRequest) {
        checkUser(userId);
        Page<ItemRequest> itemRequestsPage = itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(userId, pageRequest);
        return itemRequestsPage.stream()
                .map(itemRequest -> {
                    ItemRequestDto itemRequestDto = itemRequestMapper.itemRequestDtoFromItemRequest(itemRequest);
                    itemRequestDto.setItems(itemRequest.getItems().stream()
                            .map(item -> new ItemDto(item.getId(), item.getName())) // здесь предполагается, что у вас есть метод для маппинга Item в ItemDto
                            .collect(Collectors.toList()));
                    return itemRequestDto;
                })
                .collect(Collectors.toList());
    }*/

    private User checkUser (Long userId){
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));
    }
}
