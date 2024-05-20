package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@Data
public class ItemRequest {
    private long itemRequestId;
    private String description;
    private Long requestorId;
    //private LocalDateTime created;
}
