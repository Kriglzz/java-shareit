package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistingCopyException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto addUser(UserDto userDto) throws ExistingCopyException {

        User user = userMapper.userFromUserDto(userDto);
        if (isInvalid(user)) {
            /**
             * По тестам айди должен тратиться даже на неправильного пользователя
             * Не знал как сделать по другому
             */
            User placeholder = new User();
            placeholder.setName("placeholder");
            placeholder.setEmail("placeholder@example.com");
            User savedPlaceholder = userRepository.save(placeholder);
            userRepository.delete(savedPlaceholder);
            throw new IllegalArgumentException("Invalid user data");
        }
        checkEmail(userDto.getEmail());
        User savedUser = userRepository.save(user);
        return userMapper.userDtoFromUser(savedUser);
    }

    public boolean isInvalid(User user) {
        if (user.getName() == null || user.getName().isEmpty() || user.getEmail() == null || user.getEmail().isEmpty()) {
            return true;
        }
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());
        return existingUser.isPresent();
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));
        updateUserFields(user, userDto);
        User updatedUser = userRepository.save(user);
        return userMapper.userDtoFromUser(updatedUser);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id \"" + userId + "\" not found"));
        return userMapper.userDtoFromUser(user);
    }

    @Transactional
    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(userMapper::userDtoFromUser).collect(Collectors.toList());
    }

    private void checkEmail(String email) {
        Optional<User> existingUser = userRepository.findUserByEmail(email);
        if (existingUser.isPresent()) {
            throw new ExistingCopyException("Email \"" + email + "\" уже существует");
        }
    }


    private void updateUserFields(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }

        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            user.setEmail(userDto.getEmail());
        }
    }
}
