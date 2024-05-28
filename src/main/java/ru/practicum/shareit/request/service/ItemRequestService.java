package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, ItemRequestDto itemRequestDto);

    ItemRequestDto getById(Long userId, Long requestId);

    List<ItemRequestDto> getByOwner (Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Pageable pageRequest);

}
