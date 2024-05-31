package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "test@mail.com");
        itemRequest = new ItemRequest(1L, "description", user, LocalDateTime.now());
        itemRequestDto = new ItemRequestDto(1L, "description", 1L, LocalDateTime.now(), null);
    }

    @Test
    public void testCreateItemRequest() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestMapper.itemRequestFromItemRequestDto(any(ItemRequestDto.class))).thenReturn(itemRequest);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);
        when(itemRequestMapper.itemRequestDtoFromItemRequest(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.createItemRequest(user.getId(), itemRequestDto);

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    public void testCreateItemRequestUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.createItemRequest(user.getId(), itemRequestDto);
        });

        verify(itemRequestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    public void testGetById() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(itemRequestMapper.itemRequestDtoFromItemRequest(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRequestMapper.addItems(any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getById(user.getId(), itemRequest.getId());

        assertNotNull(result);
        assertEquals(itemRequestDto.getDescription(), result.getDescription());
        verify(itemRequestRepository, times(2)).findById(itemRequest.getId());
    }

    @Test
    public void testGetByIdUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getById(user.getId(), itemRequest.getId());
        });

        verify(itemRequestRepository, never()).findById(anyLong());
    }

    @Test
    public void testGetByIdRequestNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(itemRequest.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getById(user.getId(), itemRequest.getId());
        });

        verify(itemRequestRepository, times(1)).findById(itemRequest.getId());
    }

    @Test
    public void testGetByOwner() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdOrderByCreatedDesc(user.getId())).thenReturn(List.of(itemRequest));
        when(itemRequestMapper.itemRequestDtoFromItemRequest(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRequestMapper.addItems(any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = itemRequestService.getByOwner(user.getId());

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdOrderByCreatedDesc(user.getId());
    }

    @Test
    public void testGetByOwnerUserNotFound() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getByOwner(user.getId());
        });

        verify(itemRequestRepository, never()).findAllByRequesterIdOrderByCreatedDesc(anyLong());
    }

    @Test
    public void testGetAllRequests() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> itemRequestPage = new PageImpl<>(List.of(itemRequest));

        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRequestRepository.findAllByRequesterIdNotOrderByCreatedDesc(user.getId(), pageable)).thenReturn(itemRequestPage);
        when(itemRequestMapper.itemRequestDtoFromItemRequest(any(ItemRequest.class))).thenReturn(itemRequestDto);
        when(itemRequestMapper.addItems(any(ItemRequestDto.class))).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId(), pageable);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequesterIdNotOrderByCreatedDesc(user.getId(), pageable);
    }

    @Test
    public void testGetAllRequestsUserNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> {
            itemRequestService.getAllRequests(user.getId(), pageable);
        });

        verify(itemRequestRepository, never()).findAllByRequesterIdNotOrderByCreatedDesc(anyLong(), any(Pageable.class));
    }


}
