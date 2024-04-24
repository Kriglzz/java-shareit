package ru.practicum.shareit.user.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User userFromUserDto(UserDto userDto);

    UserDto userDtoFromUser(User user);
    /*public UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public User toUser(UserDto user) {
        return new User(user.getId(), user.getName(), user.getEmail());
    }*/
}
