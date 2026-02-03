package in.food.restro.authservice.controller;

import java.util.Collection;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.food.restro.authservice.dtos.UserDto;
import in.food.restro.authservice.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Controller for managing User-related HTTP requests.
 * <p>
 * Exposes REST endpoints for:
 * <ul>
 * <li>CRUD Operations</li>
 * <li>Search (by Email, Phone, Username)</li>
 * <li>Role Management (Assign/Remove)</li>
 * <li>Status Management (Activate/Deactivate)</li>
 * <li>Security (Change Password)</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
@Slf4j
public class UserController {
    
    private final UserService userService;
    
    /**
     * Creates a new user.
     * Endpoint: POST /api/v1/users/create
     *
     * @param userDto Request body containing user details.
     * @return ResponseEntity containing the created UserDto and HTTP status 201 (Created).
     */
    @PostMapping("/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto){
        log.info("Controller: Request to create user: {}", userDto.getUsername());
        UserDto savedUser = userService.createUser(userDto);
        log.info("Controller: User created successfully with ID: {}", savedUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }
    
    /**
     * Retrieves all users with pagination and sorting.
     * Endpoint: GET /api/v1/users/all
     *
     * @param page Page number (default 0).
     * @param size Number of records per page (default 10).
     * @param sortBy Field to sort by (default "id").
     * @param sortDir Sort direction (default "asc").
     * @return ResponseEntity containing the list of users.
     */
    @GetMapping("/all")
    public ResponseEntity<Iterable<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        log.info("Controller: Fetch all users - Page: {}, Size: {}, Sort: {}, Dir: {}", page, size, sortBy, sortDir);
        Iterable<UserDto> users = userService.getAllUsers(page, size, sortBy, sortDir);
        
        if (users instanceof Collection) {
            log.info("Controller: Count fetched: {}", ((Collection<?>) users).size());
        }
        
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves a single user by their ID.
     * Endpoint: GET /api/v1/users/{id}
     *
     * @param id The UUID of the user.
     * @return ResponseEntity containing the UserDto.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable String id) {
        log.info("Controller: Fetch user by ID: {}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves a single user by their username.
     * Endpoint: GET /api/v1/users/username/{username}
     *
     * @param username The username to search for.
     * @return ResponseEntity containing the UserDto.
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        log.info("Controller: Fetch user by username: {}", username);
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    /**
     * Searches for a user by email address.
     * Endpoint: GET /api/v1/users/search/email?value={email}
     *
     * @param email The email address to search for.
     * @return ResponseEntity containing the UserDto.
     */
    @GetMapping("/search/email")
    public ResponseEntity<UserDto> getUserByEmail(@RequestParam String email) {
        log.info("Controller: Search user by email: {}", email);
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Searches for a user by phone number.
     * Endpoint: GET /api/v1/users/search/phone?value={phoneNumber}
     *
     * @param phoneNumber The phone number to search for.
     * @return ResponseEntity containing the UserDto.
     */
    @GetMapping("/search/phone")
    public ResponseEntity<UserDto> getUserByPhoneNumber(@RequestParam String phoneNumber) {
        log.info("Controller: Search user by phone: {}", phoneNumber);
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }

    /**
     * Updates an existing user's profile information.
     * Endpoint: PUT /api/v1/users/{id}
     *
     * @param id The UUID of the user to update.
     * @param userDto Request body containing updated fields.
     * @return ResponseEntity containing the updated UserDto.
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String id, @RequestBody UserDto userDto) {
        log.info("Controller: Request to update user ID: {}", id);
        UserDto updatedUser = userService.updateUser(id, userDto);
        log.info("Controller: User updated successfully: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Deletes a user from the system.
     * Endpoint: DELETE /api/v1/users/{id}
     *
     * @param id The UUID of the user to delete.
     * @return ResponseEntity containing the deleted UserDto.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<UserDto> deleteUser(@PathVariable String id) {
        log.warn("Controller: Request to delete user ID: {}", id);
        UserDto deletedUser = userService.deleteUser(id);
        log.info("Controller: User deleted successfully: {}", id);
        return ResponseEntity.ok(deletedUser);
    }

    /**
     * Activates a user account.
     * Endpoint: PATCH /api/v1/users/{id}/activate
     *
     * @param id The UUID of the user.
     * @return ResponseEntity containing the updated UserDto.
     */
    @PatchMapping("/{id}/activate")
    public ResponseEntity<UserDto> activateUser(@PathVariable String id) {
        log.info("Controller: Request to activate user ID: {}", id);
        return ResponseEntity.ok(userService.activateUser(id));
    }

    /**
     * Deactivates a user account.
     * Endpoint: PATCH /api/v1/users/{id}/deactivate
     *
     * @param id The UUID of the user.
     * @return ResponseEntity containing the updated UserDto.
     */
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<UserDto> deactivateUser(@PathVariable String id) {
        log.info("Controller: Request to deactivate user ID: {}", id);
        return ResponseEntity.ok(userService.deactivateUser(id));
    }

    /**
     * Assigns a specific role to a user.
     * Endpoint: PUT /api/v1/users/{userId}/roles/{roleId}
     *
     * @param userId The UUID of the user.
     * @param roleId The UUID of the role to assign.
     * @return ResponseEntity containing the updated UserDto.
     */
    @PutMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDto> assignRoleToUser(@PathVariable String userId, @PathVariable String roleId) {
        log.info("Controller: Request to assign role {} to user {}", roleId, userId);
        return ResponseEntity.ok(userService.assignRoleToUser(userId, roleId));
    }

    /**
     * Removes a specific role from a user.
     * Endpoint: DELETE /api/v1/users/{userId}/roles/{roleId}
     *
     * @param userId The UUID of the user.
     * @param roleId The UUID of the role to remove.
     * @return ResponseEntity containing the updated UserDto.
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    public ResponseEntity<UserDto> removeRoleFromUser(@PathVariable String userId, @PathVariable String roleId) {
        log.info("Controller: Request to remove role {} from user {}", roleId, userId);
        return ResponseEntity.ok(userService.removeRoleFromUser(userId, roleId));
    }

    /**
     * Changes the password for a user.
     * Endpoint: PATCH /api/v1/users/{id}/password
     *
     * @param id The UUID of the user.
     * @param payload Map containing the key "password".
     * @return ResponseEntity containing the updated UserDto.
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<UserDto> changePassword(@PathVariable String id, @RequestBody Map<String, String> payload) {
        log.info("Controller: Request to change password for ID: {}", id);
        String newPassword = payload.get("password");
        
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }
        
        UserDto user = userService.changeUserPassword(id, newPassword);
        return ResponseEntity.ok(user);
    }

    /**
     * Checks if a username exists in the system.
     * Endpoint: GET /api/v1/users/exists/username?value={username}
     *
     * @param username The username to check.
     * @return ResponseEntity containing boolean true/false.
     */
    @GetMapping("/exists/username")
    public ResponseEntity<Boolean> checkUsernameExists(@RequestParam String username) {
        log.debug("Controller: Checking existence of username: {}", username);
        return ResponseEntity.ok(userService.isUsernameExists(username));
    }

    /**
     * Checks if an email exists in the system.
     * Endpoint: GET /api/v1/users/exists/email?value={email}
     *
     * @param email The email to check.
     * @return ResponseEntity containing boolean true/false.
     */
    @GetMapping("/exists/email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.debug("Controller: Checking existence of email: {}", email);
        return ResponseEntity.ok(userService.isEmailExists(email));
    }
    
    /**
     * Retrieves user details including role information.
     * Endpoint: GET /api/v1/users/{id}/details
     *
     * @param id The UUID of the user.
     * @return ResponseEntity containing the UserDto.
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<UserDto> getUserRoleDetailsById(@PathVariable String id) {
        log.info("Controller: Fetch role details for user ID: {}", id);
        return ResponseEntity.ok(userService.getUserRoleDetailsById(id));
    }
}