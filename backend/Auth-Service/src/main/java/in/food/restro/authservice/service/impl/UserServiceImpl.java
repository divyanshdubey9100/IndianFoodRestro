package in.food.restro.authservice.service.impl;

import java.time.Instant;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.food.restro.authservice.dtos.UserDto;
import in.food.restro.authservice.entities.Role;
import in.food.restro.authservice.entities.User;
import in.food.restro.authservice.enums.Provider;
import in.food.restro.authservice.repositories.UserRepository;
import in.food.restro.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        log.info("Attempting to create user with username: {} and email: {}", userDto.getUsername(), userDto.getEmail());
        
        // 1. Validation
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()
                || userDto.getUsername() == null || userDto.getUsername().isBlank()
                || userDto.getPassword() == null || userDto.getPassword().isBlank()) {
            log.error("Validation failed: Email, Username, or Password missing.");
            throw new IllegalArgumentException("Email, Username and Password are required fields.");
        }

        // 2. Check Existence
        if (isEmailExists(userDto.getEmail())) {
            log.warn("Registration failed: Email {} already exists.", userDto.getEmail());
            throw new IllegalArgumentException("Email already exists.");
        }

        if (isUsernameExists(userDto.getUsername())) {
            log.warn("Registration failed: Username {} already exists.", userDto.getUsername());
            throw new IllegalArgumentException("Username already exists.");
        }

        // 3. Map DTO to Entity
        User user = modelMapper.map(userDto, User.class);
        
        // 4. Set Default Values
        user.setProvider(userDto.getProvider() != null ? userDto.getProvider() : Provider.LOCAL);
        user.setActive(true); 
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully with ID: {}", savedUser.getId());
        
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto assignRoleToUser(String userId, String roleId) {
        log.info("Assigning role ID: {} to user ID: {}", roleId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        Role role = new Role();
        role.setId(UUID.fromString(roleId)); 
        
        user.getRoles().add(role);

        User savedUser = userRepository.save(user);
        log.info("Role assigned successfully. User {} now has {} roles.", userId, savedUser.getRoles().size());
        
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    @Transactional
    public UserDto removeRoleFromUser(String userId, String roleId) {
        log.info("Removing role ID: {} from user ID: {}", roleId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> {
                    log.error("User not found with id: {}", userId);
                    return new RuntimeException("User not found with id: " + userId);
                });

        UUID rId = UUID.fromString(roleId);
        boolean removed = user.getRoles().removeIf(role -> role.getId().equals(rId));
        
        if(removed) {
             log.info("Role removed successfully from user: {}", userId);
        } else {
             log.warn("Role ID: {} was not found on user: {}", roleId, userId);
        }
        
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto getUserById(String id) {
        log.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        log.debug("Fetching user with username: {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        return modelMapper.map(user, UserDto.class);
    }
    
    @Override
    public UserDto getUserByPhoneNumber(String phoneNumber) {
         log.debug("Fetching user with phone number: {}", phoneNumber);
         User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone: " + phoneNumber));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public UserDto updateUser(String id, UserDto userDto) {
        log.info("Updating user profile for ID: {}", id);
        
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setFirstName(userDto.getFirstName());
        user.setMiddleName(userDto.getMiddleName());
        user.setLastName(userDto.getLastName());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setGender(userDto.getGender());
        user.setDob(userDto.getDob());
        user.setProfileImage(userDto.getProfileImage());
        
        if(userDto.getAddress() != null) {
             // Address logic here if needed
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", id);
        return modelMapper.map(updatedUser, UserDto.class);
    }

    @Override
    public UserDto deleteUser(String id) {
        log.warn("Deleting user with ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        UserDto deletedUserDto = modelMapper.map(user, UserDto.class);
        userRepository.delete(user);
        log.info("User deleted successfully: {}", id);
        return deletedUserDto;
    }

    @Override
    public UserDto activateUser(String id) {
        log.info("Activating user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setActive(true);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto deactivateUser(String id) {
        log.info("Deactivating user ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setActive(false);
        User savedUser = userRepository.save(user);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public UserDto changeUserPassword(String id, String newPassword) {
        log.info("Changing password for user ID: {}", id); // Don't log the new password!
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setPassword(newPassword); 
        User savedUser = userRepository.save(user);
        log.info("Password changed successfully for user: {}", id);
        return modelMapper.map(savedUser, UserDto.class);
    }

    @Override
    public void updateLastLoginAt(String id) {
        log.debug("Updating last login time for user: {}", id);
        userRepository.findById(UUID.fromString(id)).ifPresent(user -> {
            user.setLastLoginAt(Instant.now());
            userRepository.save(user);
        });
    }

    @Override
    public boolean isUsernameExists(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneNumberExists(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public boolean isUserIdExists(String id) {
        return userRepository.existsById(UUID.fromString(id));
    }

    @Override
    public Iterable<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDir) {
        log.debug("Fetching all users - Page: {}, Size: {}, Sort: {}", pageNumber, pageSize, sortBy);
        Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        
        Page<User> page = userRepository.findAll(pageable);
        
        return page.getContent().stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserRoleDetailsById(String id) {
        log.debug("Fetching user details with roles for ID: {}", id);
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("User not found"));
        return modelMapper.map(user, UserDto.class);
    }
}