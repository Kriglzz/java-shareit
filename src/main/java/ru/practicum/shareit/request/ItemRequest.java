package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@Data
public class ItemRequest {
    private long id;
    private String description;
    private String requestor;
    private LocalDateTime created;
}
