package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;


    UserDto userDto;

    User user;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "user1",
                "user1@mail.ru");
        user = new User(
                1L,
                "user1",
                "user1@mail.ru");
    }

    @Test
    public void testAddUser_WithExistingEmail() {
        UserDto userDto = new UserDto();
        userDto.setName("New User");
        userDto.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setName("exist");
        existingUser.setEmail("existing@example.com");

        when(userMapper.userFromUserDto(userDto)).thenReturn(existingUser);
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> userService.addUser(userDto));
    }

    @Test
    public void testUpdateUser() {
        User updatedUser = new User(
                1L,
                "Amo gus",
                "amogus@sus.com");
        UserDto updatedUserDto = userDto = new UserDto(
                1L,
                "Amo gus",
                "amogus@sus.com");

        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userRepository.save(user)).thenReturn(updatedUser);
        when(userMapper.userDtoFromUser(updatedUser)).thenReturn(updatedUserDto);

        UserDto result = userService.updateUser(1L, updatedUserDto);


        assertEquals("Amo gus", result.getName());
        assertEquals("amogus@sus.com", result.getEmail());
    }

    @Test
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.ofNullable(user));
        when(userMapper.userDtoFromUser(user)).thenReturn(userDto);
        when(userMapper.userFromUserDto(userDto)).thenReturn(user);

        UserDto foundUser = userService.getUserById(user.getId());

        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void testDeleteUserById() {
        when(userMapper.userFromUserDto(userDto)).thenReturn(user);
        ;
        userService.deleteUserById(user.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void testGetAllUsers() {
        User user2 = new User(2L, "user2", "user2@mail.ru");
        UserDto userDto2 = new UserDto(2L, "user2", "user2@mail.ru");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));
        when(userMapper.userDtoFromUser(user)).thenReturn(userDto);
        when(userMapper.userDtoFromUser(user2)).thenReturn(userDto2);

        assertEquals(2, userService.getAllUsers().size());
        assertTrue(userService.getAllUsers().contains(userDto));
        assertTrue(userService.getAllUsers().contains(userDto2));
    }

    @Test
    void testUpdateUserNotFound() {
        UserDto updatedUserDto = new UserDto(99L, "updatedName", "updated@mail.com");
        NotFoundException thrown = assertThrows(NotFoundException.class, () -> {
            userService.updateUser(99L, updatedUserDto);
        });
        assertEquals("Пользователь с id \"99\" не найден", thrown.getMessage());
    }


}
