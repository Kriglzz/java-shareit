package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@Data
public class ItemRequest {
    private long itemRequest_id;
    private String description;
    private Long requestor_id;
    //private LocalDateTime created;
}
