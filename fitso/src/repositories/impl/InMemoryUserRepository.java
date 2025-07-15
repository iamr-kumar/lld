package fitso.src.repositories.impl;

import fitso.src.models.User;
import fitso.src.repositories.UserRepository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of UserRepository using ConcurrentHashMap for thread
 * safety.
 */
public class InMemoryUserRepository implements UserRepository {

    private final ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> emailToUserId = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Check if email is already used by another user
        String existingUserId = emailToUserId.get(user.getEmail());
        if (existingUserId != null && !existingUserId.equals(user.getUserId())) {
            throw new IllegalArgumentException("Email already exists for another user");
        }

        users.put(user.getUserId(), user);
        emailToUserId.put(user.getEmail(), user.getUserId());
        return user;
    }

    @Override
    public Optional<User> findById(String userId) {
        if (userId == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String userId = emailToUserId.get(email);
        return userId != null ? Optional.ofNullable(users.get(userId)) : Optional.empty();
    }

    @Override
    public boolean existsById(String userId) {
        return userId != null && users.containsKey(userId);
    }

    @Override
    public boolean deleteById(String userId) {
        if (userId == null) {
            return false;
        }

        User user = users.remove(userId);
        if (user != null) {
            emailToUserId.remove(user.getEmail());
            return true;
        }
        return false;
    }
}
