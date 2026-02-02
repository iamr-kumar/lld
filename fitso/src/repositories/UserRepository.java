package repositories;

import models.User;
import java.util.Optional;

/**
 * Repository interface for User data access operations.
 * Following Repository pattern for data abstraction.
 */
public interface UserRepository {

    /**
     * Saves a user to the repository.
     * 
     * @param user the user to save
     * @return the saved user
     */
    User save(User user);

    /**
     * Finds a user by their unique ID.
     * 
     * @param userId the user ID to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findById(String userId);

    /**
     * Finds a user by their email.
     * 
     * @param email the email to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if a user exists with the given ID.
     * 
     * @param userId the user ID to check
     * @return true if user exists, false otherwise
     */
    boolean existsById(String userId);

    /**
     * Deletes a user by their ID.
     * 
     * @param userId the user ID to delete
     * @return true if user was deleted, false if not found
     */
    boolean deleteById(String userId);
}
