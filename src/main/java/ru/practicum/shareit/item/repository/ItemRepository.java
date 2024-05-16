package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    /*Item getItemById(Long itemId);
        Вроде бы не нужно?
    void deleteItemById(Long itemId);*/

    List<Item> findAllByOwnerId(Long ownerId, Sort sort);

    @Query("SELECT i FROM Item i WHERE" +
            " (LOWER(i.name) LIKE %:string% OR LOWER(i.description) LIKE %:string%)" +
            " AND i.available = true")
    List<Item> findByNameOrDescriptionContainingIgnoreCase(String string);
}
