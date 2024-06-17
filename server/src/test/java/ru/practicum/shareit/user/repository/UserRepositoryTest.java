package ru.practicum.shareit.user.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        user1 = userRepository.save(new User(1L, "user1", "user1@mail.ru"));
        user2 = userRepository.save(new User(2L, "user2", "user2@mail.ru"));
    }

    @Test
    void testFindUserByEmail() {
        Optional<User> foundUser1 = userRepository.findUserByEmail("user1@mail.ru");
        Optional<User> foundUser2 = userRepository.findUserByEmail("user2@mail.ru");
        Optional<User> notFoundUser = userRepository.findUserByEmail("nonexistent@mail.ru");

        assertThat(foundUser1).isPresent().contains(user1);
        assertThat(foundUser2).isPresent().contains(user2);
        assertThat(notFoundUser).isNotPresent();
    }

    @Test
    void testFindByEmail() {
        Optional<User> foundUser1 = userRepository.findByEmail("user1@mail.ru");
        Optional<User> foundUser2 = userRepository.findByEmail("user2@mail.ru");
        Optional<User> notFoundUser = userRepository.findByEmail("nonexistent@mail.ru");

        assertThat(foundUser1).isPresent().contains(user1);
        assertThat(foundUser2).isPresent().contains(user2);
        assertThat(notFoundUser).isNotPresent();
    }
}
