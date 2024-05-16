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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    /*    private final Map<Long, User> users = new HashMap<>();*/
    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /*@Override
    public UserDto addUser(UserDto user) throws ExistingCopyException {
        checkEmail(user.getEmail());
        user.setId(id++);
        users.put(user.getId(), userMapper.userFromUserDto(user));
        return user;
    }
*/

    @Override
    public UserDto addUser(UserDto userDto) throws ExistingCopyException {
        checkEmail(userDto.getEmail());
        User user = userMapper.userFromUserDto(userDto);
        User savedUser = userRepository.save(user);
        return userMapper.userDtoFromUser(savedUser);
    }

    /*@Override
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
    }*/

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id \"" + userId + "\" не найден"));
        updateUserFields(user, userDto);
        User updatedUser = userRepository.save(user);
        return userMapper.userDtoFromUser(updatedUser);
    }


    /*@Override
    public UserDto getUserById(Long userId) {
        checkId(userId);
        return userMapper.userDtoFromUser(users.get(userId));
    }*/

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id \"" + userId + "\" not found"));
        return userMapper.userDtoFromUser(user);
    }

    /*@Override
    public void deleteUserById(Long userId) {
        checkId(userId);
        users.remove(userId);
    }*/

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    /*@Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(userMapper::userDtoFromUser)
                .collect(Collectors.toList());
    }*/

    @Override
    public List<UserDto> getAllUsers() {
        List<User> allUsers = userRepository.findAll();
        return allUsers.stream().map(userMapper::userDtoFromUser).collect(Collectors.toList());
    }

    /*private void checkEmail(String email) {
        if (users.values().stream().anyMatch((user) -> user.getEmail().equals(email))) {
            throw new ExistingCopyException("Email \"" + email + "\" уже существует");
        }
    }*/

    private void checkEmail(String email) {
        User existingUser = userRepository.findUserByEmail(email);
        if (existingUser != null) {
            throw new ExistingCopyException("Email \"" + email + "\" уже существует");
        }
    }

/*    private void checkId(Long userId) {
        if (users.values().stream().noneMatch((user) -> Objects.equals(user.getId(), userId))) {
            throw new NotFoundException("Пользователь с id \"" + userId + "\" не найден");
        }
    }*/

    private void updateUserFields(User user, UserDto userDto) {
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()
                && !userDto.getEmail().equals(user.getEmail())) {
            checkEmail(userDto.getEmail());
            user.setEmail(userDto.getEmail());
        }
    }
}
