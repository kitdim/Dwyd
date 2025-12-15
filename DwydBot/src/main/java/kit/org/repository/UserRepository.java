package kit.org.repository;

import kit.org.bot.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserRepository implements Repository<User> {
    private static volatile UserRepository instance;
    private final List<User> users = new ArrayList<>();

    // Приватный конструктор
    private UserRepository() {
        // Инициализация при необходимости
    }

    public static UserRepository getInstance() {
        if (instance == null) {
            synchronized (UserRepository.class) {
                if (instance == null) {
                    instance = new UserRepository();
                }
            }
        }
        return instance;
    }

    public boolean save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        boolean userExists = users.stream()
                .anyMatch(existingUser -> existingUser.getChatId().equals(user.getChatId()));

        if (userExists) {
            return false;
        }

        return users.add(user);
    }

    @Override
    public boolean update(User value) {
        return false;
    }

    @Override
    public boolean delete(User value) {
        return false;
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        return List.of();
    }
}
