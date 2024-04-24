package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {
    UserDto addUser(UserDto user);

    UserDto updateUser(Long userId, UserDto user);

    UserDto getUserById(Long userId);

    void deleteUserById(Long userId);

    List<UserDto> getAllUsers();
}
