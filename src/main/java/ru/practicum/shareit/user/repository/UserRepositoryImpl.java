package ru.practicum.shareit.user.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ExistingCopyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final UserMapper userMapper;
    private Long id = 1L;

    @Override
    public UserDto addUser(UserDto user) throws ExistingCopyException {
        checkEmail(user.getEmail());
        user.setId(id++);
        users.put(user.getId(), userMapper.userFromUserDto(user));
        return user;
    }

    @Override
    public UserDto updateUser(Long userId, UserDto user) {
        checkId(userId);
        User updated = users.get(userId);
        if (!(user.getName() == null || user.getName().isEmpty())) {
            updated.setName(user.getName());
        }
        if (!(user.getEmail() == null || user.getEmail().equals(updated.getEmail()) || user.getEmail().isEmpty())) {
            checkEmail(user.getEmail());
            updated.setEmail(user.getEmail());
        }
        users.put(userId, updated);
        return userMapper.userDtoFromUser(updated);
    }

    @Override
    public UserDto getUserById(Long userId) {
        checkId(userId);
        return userMapper.userDtoFromUser(users.get(userId));
    }

    @Override
    public void deleteUserById(Long userId) {
        checkId(userId);
        users.remove(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(userMapper::userDtoFromUser)
                .collect(Collectors.toList());
    }

    private void checkEmail(String email) {
        if (users.values().stream().anyMatch((user) -> user.getEmail().equals(email))) {
            throw new ExistingCopyException("Email \"" + email + "\" уже существует");
        }
    }

    private void checkId(Long userId) {
        if (users.values().stream().noneMatch((user) -> Objects.equals(user.getId(), userId))) {
            throw new NotFoundException("Пользователь с id \"" + userId + "\" не найден");
        }
    }
}
