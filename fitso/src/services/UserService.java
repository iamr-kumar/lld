package services;

import models.User;
import java.util.Optional;

/**
 * Service interface for User-related business operations.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * 
     * @param userId unique identifier for the user
     * @param name   user's name
     * @param email  user's email address
     * @return the registered user
     * @throws IllegalArgumentException if user already exists or email is already
     *                                  used
     */
    User registerUser(String userId, String name, String email);

    /**
     * Finds a user by their ID.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findUserById(String userId);

    /**
     * Finds a user by their email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findUserByEmail(String email);

    /**
     * Validates if a user exists in the system.
     * 
     * @param userId the user ID to validate
     * @return true if user exists, false otherwise
     */
    boolean validateUser(String userId);
}
