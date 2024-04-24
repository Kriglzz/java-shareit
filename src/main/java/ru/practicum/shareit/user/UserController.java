package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepository;

    /*@PostMapping
    public ResponseEntity<ResponseWrapper<UserDto>> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userRepository.addUser(userDto);
        return new ResponseEntity<>(new ResponseWrapper<>(user), HttpStatus.OK);
    }*/

    @PostMapping
    public ResponseEntity<UserDto> addUser(@Valid @RequestBody UserDto userDto) {
        UserDto user = userRepository.addUser(userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /*@PatchMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<UserDto>> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto user = userRepository.updateUser(userId, userDto);
        return new ResponseEntity<>(new ResponseWrapper<>(user), HttpStatus.OK);
    }*/

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto user = userRepository.updateUser(userId, userDto);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /*@GetMapping
    public ResponseEntity<ResponseWrapper<List<UserDto>>> getAllUsers() {
        List<UserDto> users = userRepository.getAllUsers();
        return new ResponseEntity<>(new ResponseWrapper<>(users), HttpStatus.FOUND);
    }*/

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

 /*   @GetMapping("/{userId}")
    public ResponseEntity<ResponseWrapper<UserDto>> getUserById(@PathVariable Long userId) {
        UserDto user = userRepository.getUserById(userId);
        return new ResponseEntity<>(new ResponseWrapper<>(user), HttpStatus.FOUND);
    }*/

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto user = userRepository.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userRepository.deleteUserById(userId);
    }
}
