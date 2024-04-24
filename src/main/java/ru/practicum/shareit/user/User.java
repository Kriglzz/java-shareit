package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@Data
public class User {
    private long id;
    private String name;
    private String email;
}
