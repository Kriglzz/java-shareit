package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import static org.junit.jupiter.api.Assertions.*;


@Transactional
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceImplTest {

    private final UserService userService;

    UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(
                1L,
                "user1",
                "user1@mail.ru");
    }

    @Test
    void updateUserTest() {
        UserDto user = userService.addUser(userDto);
        UserDto updatedUserDto = new UserDto(user.getId(), "updatedName", "amogus@sus");
        UserDto updatedUser = userService.updateUser(user.getId(), updatedUserDto);
        assertEquals("amogus@sus", updatedUser.getEmail());
        assertEquals("updatedName", updatedUser.getName());
    }

    @Test
    void testGetUserById() {
        UserDto user = userService.addUser(userDto);
        UserDto foundUser = userService.getUserById(user.getId());
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getName(), foundUser.getName());
        assertEquals(user.getEmail(), foundUser.getEmail());
    }

    @Test
    void testDeleteUserById() {
        UserDto user = userService.addUser(userDto);
        userService.deleteUserById(user.getId());
        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void testGetAllUsers() {
        userService.addUser(userDto);
        UserDto userDto2 = userService.addUser(new UserDto(2L, "user2", "user2@mail.ru"));
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
