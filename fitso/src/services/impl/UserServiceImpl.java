package fitso.src.services.impl;

import fitso.src.models.User;
import fitso.src.repositories.UserRepository;
import fitso.src.services.UserService;
import java.util.Optional;

/**
 * Implementation of UserService with business logic for user operations.
 */
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(String userId, String name, String email) {
        // Validate input parameters
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }

        // Check if user already exists
        if (userRepository.existsById(userId)) {
            throw new IllegalArgumentException("User already exists with ID: " + userId);
        }

        // Check if email is already used
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }

        // Create and save user
        User user = new User(userId.trim(), name.trim(), email.trim());
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findUserById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean validateUser(String userId) {
        return userRepository.existsById(userId);
    }
}
