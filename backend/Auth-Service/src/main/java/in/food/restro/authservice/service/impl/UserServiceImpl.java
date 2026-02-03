package in.food.restro.authservice.service.impl;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.food.restro.authservice.dtos.RoleDto;
import in.food.restro.authservice.dtos.UserDto;
import in.food.restro.authservice.entities.Role;
import in.food.restro.authservice.entities.User;
import in.food.restro.authservice.enums.Provider;
import in.food.restro.authservice.exception.ResourceNotFoundException;
import in.food.restro.authservice.repositories.UserRepository;
import in.food.restro.authservice.service.UserService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service Implementation for User Management.
 * <p>
 * This class handles the core business logic for the User module, including:
 * <ul>
 * <li>CRUD operations (Create, Read, Update, Delete)</li>
 * <li>Role management (Assigning/Removing roles via EntityManager to prevent duplicates)</li>
 * <li>Account status management (Activate/Deactivate)</li>
 * <li>Security operations (Password changes)</li>
 * <li>Search and Existence checks</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;

    /**
     * Creates a new user in the system.
     * <p>
     * Performs validation on required fields and checks for duplicate Email/Username.
     * Uses {@link EntityManager} to check if roles already exist in the database to avoid
     * 'Duplicate Key' constraint violations.
     *
     * @param userDto Data Transfer Object containing user registration details.
     * @return UserDto The created user.
     * @throws IllegalArgumentException If validation fails or if Email/Username already exists.
     */
    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Service: Attempting to create user with username: {}", userDto.getUsername());

        if (userDto.getEmail() == null || userDto.getEmail().isBlank()
                || userDto.getUsername() == null || userDto.getUsername().isBlank()
                || userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            log.error("Service: Validation failed. Required fields (Email, Username, Password) are missing.");
            throw new IllegalArgumentException("Email, Username, and Password are required fields.");
        }

        if (userRepository.existsByEmail(userDto.getEmail())) {
            log.warn("Service: Registration failed. Email {} already exists.", userDto.getEmail());
            throw new IllegalArgumentException("Email already exists.");
        }
        if (userRepository.existsByUsername(userDto.getUsername())) {
            log.warn("Service: Registration failed. Username {} already exists.", userDto.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }

        User user = modelMapper.map(userDto, User.class);

        Set<Role> managedRoles = new HashSet<>();
        if (userDto.getRoles() != null) {
            for (RoleDto roleDto : userDto.getRoles()) {
                // Check if role exists in DB to prevent duplicate insertion
                List<Role> existingRoles = entityManager.createQuery(
                        "SELECT r FROM Role r WHERE r.name = :name", Role.class)
                        .setParameter("name", roleDto.getName())
                        .getResultList();

                if (!existingRoles.isEmpty()) {
                    managedRoles.add(existingRoles.get(0));
                } else {
                    log.debug("Service: Role '{}' not found in DB, creating new entity.", roleDto.getName());
                    Role newRole = new Role();
                    newRole.setName(roleDto.getName());
                    managedRoles.add(newRole);
                }
            }
        }
        user.setRoles(managedRoles);
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("Service: User created successfully with ID: {}", savedUser.getId());
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Updates an existing user's profile information.
     *
     * @param id The UUID of the user to update.
     * @param userDto DTO containing updated fields.
     * @return UserDto The updated user details.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        log.info("Service: Request to update user profile for ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("Service: Update failed. User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        user.setFirstName(userDto.getFirstName());
        user.setMiddleName(userDto.getMiddleName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setGender(userDto.getGender());
        user.setDob(userDto.getDob());
        user.setProfileImage(userDto.getProfileImage());

        User updatedUser = userRepository.save(user);
        log.info("Service: User profile updated successfully for ID: {}", id);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    /**
     * Deletes a user by their unique ID.
     *
     * @param id The UUID of the user to delete.
     * @return UserDto The details of the user that was deleted.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto deleteUser(String id) {
        log.warn("Service: Request to delete user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("Service: Delete failed. User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });

        UserDto deletedUserDto = modelMapper.map(user, UserDto.class);
        userRepository.delete(user);
        log.info("Service: User deleted successfully: {}", id);
        return deletedUserDto;
    }

    /**
     * Retrieves a user by their unique ID.
     *
     * @param id The UUID of the user.
     * @return UserDto The found user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto getUserById(String id) {
        log.debug("Service: Fetching user by ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> {
                    log.error("Service: User not found with ID: {}", id);
                    return new ResourceNotFoundException("User not found with id: " + id);
                });
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email The email to search for.
     * @return UserDto The found user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto getUserByEmail(String email) {
        log.debug("Service: Fetching user by email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Service: User not found with email: {}", email);
                    return new ResourceNotFoundException("User not found with email: " + email);
                });
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param username The username to search for.
     * @return UserDto The found user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto getUserByUsername(String username) {
        log.debug("Service: Fetching user by username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("Service: User not found with username: {}", username);
                    return new ResourceNotFoundException("User not found with username: " + username);
                });
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Retrieves a user by their phone number.
     *
     * @param phoneNumber The phone number to search for.
     * @return UserDto The found user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto getUserByPhoneNumber(String phoneNumber) {
        log.debug("Service: Fetching user by phone: {}", phoneNumber);
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.error("Service: User not found with phone: {}", phoneNumber);
                    return new ResourceNotFoundException("User not found with phone: " + phoneNumber);
                });
        return modelMapper.map(user, UserDto.class);
    }

    /**
     * Retrieves a paginated and sorted list of all users.
     *
     * @param pageNumber The page index (0-based).
     * @param pageSize The size of the page.
     * @param sortBy The property to sort by.
     * @param sortDir The direction of sorting ("asc" or "desc").
     * @return Iterable<UserDto> A list of users.
     */
    @Override
    public Iterable<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        log.debug("Service: Fetching all users - Page: {}, Size: {}, Sort: {}, Dir: {}", pageNumber, pageSize, sortBy, sortDir);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        Page<User> page = userRepository.findAll(pageable);
        
        log.info("Service: Successfully fetched {} users.", page.getNumberOfElements());
        return page.getContent().stream()
                .map(u -> modelMapper.map(u, UserDto.class))
                .collect(Collectors.toList());
    }

    /**
     * Activates a user account (sets isActive = true).
     *
     * @param id The UUID of the user.
     * @return UserDto The updated user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto activateUser(String id) {
        log.info("Service: Request to activate user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(true);
        User savedUser = userRepository.save(user);
        log.info("Service: User ID {} is now ACTIVE.", id);
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Deactivates a user account (sets isActive = false).
     *
     * @param id The UUID of the user.
     * @return UserDto The updated user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto deactivateUser(String id) {
        log.info("Service: Request to deactivate user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.setActive(false);
        User savedUser = userRepository.save(user);
        log.info("Service: User ID {} is now INACTIVE.", id);
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Assigns an existing role to a user.
     * Uses EntityManager to fetch the Role entity to ensure it attaches to the existing database record.
     *
     * @param userId The UUID of the user.
     * @param roleId The UUID of the role to assign.
     * @return UserDto The updated user.
     * @throws ResourceNotFoundException If user or role is not found.
     */
    @Override
    @Transactional
    public UserDto assignRoleToUser(String userId, String roleId) {
        log.info("Service: Assigning role ID {} to user ID {}", roleId, userId);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        Role role = entityManager.find(Role.class, UUID.fromString(roleId));
        if (role == null) {
            log.error("Service: Role assignment failed. Role not found with ID: {}", roleId);
            throw new ResourceNotFoundException("Role not found with id: " + roleId);
        }

        user.getRoles().add(role);
        User savedUser = userRepository.save(user);
        log.info("Service: Role assigned successfully. User now has {} roles.", savedUser.getRoles().size());
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Removes a role from a user.
     *
     * @param userId The UUID of the user.
     * @param roleId The UUID of the role to remove.
     * @return UserDto The updated user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    @Transactional
    public UserDto removeRoleFromUser(String userId, String roleId) {
        log.info("Service: Removing role ID {} from user ID {}", roleId, userId);
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        boolean removed = user.getRoles().removeIf(r -> r.getId().equals(UUID.fromString(roleId)));
        
        if (removed) {
            log.info("Service: Role removed successfully.");
        } else {
            log.warn("Service: Role ID {} was not found on user {}", roleId, userId);
        }

        return modelMapper.map(userRepository.save(user), UserDto.class);
    }

    /**
     * Changes the password for a user.
     *
     * @param id The UUID of the user.
     * @param newPassword The new password string.
     * @return UserDto The updated user.
     * @throws ResourceNotFoundException If the user is not found.
     */
    @Override
    public UserDto changeUserPassword(String id, String newPassword) {
        log.info("Service: Request to change password for user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        
        user.setPassword(newPassword);
        User savedUser = userRepository.save(user);
        log.info("Service: Password changed successfully for user ID: {}", id);
        return modelMapper.map(savedUser, UserDto.class);
    }

    /**
     * Retrieves user details including roles by ID.
     * Wrapper for {@link #getUserById(String)} but explicit for clarity.
     *
     * @param id The UUID of the user.
     * @return UserDto The found user.
     */
    @Override
    public UserDto getUserRoleDetailsById(String id) {
        log.debug("Service: Fetching user details with roles for ID: {}", id);
        return getUserById(id);
    }

    /**
     * Updates the last login timestamp for a user.
     * Logs a warning if the user is not found but does not throw an exception.
     *
     * @param id The UUID of the user.
     */
    @Override
    public void updateLastLoginAt(String id) {
        log.debug("Service: Updating last login time for user ID: {}", id);
        userRepository.findById(UUID.fromString(id)).ifPresentOrElse(user -> {
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
            log.debug("Service: Last login time updated for user ID: {}", id);
        }, () -> {
            log.warn("Service: Failed to update last login. User not found with ID: {}", id);
        });
    }

    /**
     * Checks if a username already exists.
     *
     * @param username The username to check.
     * @return boolean True if exists, false otherwise.
     */
    @Override
    public boolean isUsernameExists(String username) {
        boolean exists = userRepository.existsByUsername(username);
        log.debug("Service: Checking existence of username '{}': {}", username, exists);
        return exists;
    }

    /**
     * Checks if an email already exists.
     *
     * @param email The email to check.
     * @return boolean True if exists, false otherwise.
     */
    @Override
    public boolean isEmailExists(String email) {
        boolean exists = userRepository.existsByEmail(email);
        log.debug("Service: Checking existence of email '{}': {}", email, exists);
        return exists;
    }
    
    /**
     * Checks if a phone number already exists.
     *
     * @param phoneNumber The phone number to check.
     * @return boolean True if exists, false otherwise.
     */
    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        boolean exists = userRepository.existsByPhoneNumber(phoneNumber);
        log.debug("Service: Checking existence of phone number '{}': {}", phoneNumber, exists);
        return exists;
    }

    /**
     * Checks if a user ID exists.
     *
     * @param id The UUID to check.
     * @return boolean True if exists, false otherwise.
     */
    @Override
    public boolean isUserIdExists(String id) {
        boolean exists = userRepository.existsById(UUID.fromString(id));
        log.debug("Service: Checking existence of user ID '{}': {}", id, exists);
        return exists;
    }
}