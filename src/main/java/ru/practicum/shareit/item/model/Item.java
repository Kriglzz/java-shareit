package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;

    private String name;

    private String description;

    private Boolean available;

    @ManyToOne
    @JoinColumn(name = "owner")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    @OneToOne(mappedBy = "item", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Comment comment;

}
