package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> addUser(@RequestBody UserDto userDto) {
        return userClient.postUser(userDto);
    }


    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable Long userId, @RequestBody UserDto user) {
        return userClient.updateUser(userId, user);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return userClient.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable Long userId) {
        return userClient.getUserById(userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUserById(@PathVariable Long userId) {
        return userClient.deleteUserById(userId);
    }
}
