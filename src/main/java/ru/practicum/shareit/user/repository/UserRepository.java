package ru.practicum.shareit.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);

    @Query(value = "SELECT COALESCE(MAX(user_id), 0) FROM users", nativeQuery = true)
    Long findMaxId();

    Optional<User> findByEmail(String email);
}
