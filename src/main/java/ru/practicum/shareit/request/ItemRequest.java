package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@Data
@Entity
public class ItemRequest {
    private long itemRequest_id;
    private String description;
    private Long requestor_id;
    //private LocalDateTime created;
}
