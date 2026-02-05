package in.food.restro.authservice.service.impl;


import org.springframework.stereotype.Service;
import in.food.restro.authservice.dtos.UserDto;
import in.food.restro.authservice.exception.AuthenticationFailedException;
import in.food.restro.authservice.exception.ResourceNotFoundException;
import in.food.restro.authservice.exception.UserAlreadyExistsException;
import in.food.restro.authservice.service.AuthService;
import in.food.restro.authservice.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import in.food.restro.authservice.utils.JwtHelper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtHelper jwtHelper;

    @Override
    public UserDto registerUser(UserDto userDto) {
        log.info("AuthService: Request received to register user with username: {}", userDto.getUsername());

        try {
            // 1. Check if user already exists (Assuming UserService has this method or handles the check)
            // Ideally, UserService should throw UserAlreadyExistsException, but we add a safety check if possible.
            // For this implementation, we rely on UserService logic but handle the error.
            
            // 2. Encrypt the password before sending to UserService
            String rawPassword = userDto.getPassword();
            userDto.setPassword(passwordEncoder.encode(rawPassword));
            log.debug("AuthService: Password encrypted successfully for username: {}", userDto.getUsername());

            // 3. Persist User
            UserDto createdUser = userService.createUser(userDto);
            
            // 4. Sanitize return object (Never return the password hash in the response)
            createdUser.setPassword(null);

            log.info("AuthService: User registered successfully with ID: {}", createdUser.getId());
            return createdUser;

        } catch (UserAlreadyExistsException e) {
            log.error("AuthService: Registration failed. Username '{}' already exists.", userDto.getUsername());
            throw e;
        } catch (Exception e) {
            log.error("AuthService: Unexpected error during registration for username: {}", userDto.getUsername(), e);
            throw new RuntimeException("Registration failed due to an internal error.");
        }
    }

    /**
     * Validates credentials. Returns UserDto if valid, throws Exception if not.
     */
    @Override
    public UserDto authenticateUser(String username, String password) {
        log.info("AuthService: Authenticating user: {}", username);

        // 1. Retrieve User
        UserDto userDto;
        try {
            userDto = userService.getUserByUsername(username);
        } catch (ResourceNotFoundException e) {
            log.warn("AuthService: Authentication failed. Username '{}' not found.", username);
            // We throw a generic Auth Failed exception to prevent "User Enumeration Attacks"
            throw new AuthenticationFailedException("Invalid username or password");
        }

        // 2. Match Passwords
        if (!passwordEncoder.matches(password, userDto.getPassword())) {
            log.warn("AuthService: Authentication failed. Password mismatch for username: {}", username);
            throw new AuthenticationFailedException("Invalid username or password");
        }

        log.info("AuthService: Credentials validated successfully for user: {}", username);
        return userDto;
    }

    /**
     * Orchestrates authentication and Token generation.
     */
    @Override
    public UserDto loginUser(String username, String password) {
        log.info("AuthService: Login request received for user: {}", username);

        // 1. Authenticate (Reuse the method above)
        UserDto userDto = authenticateUser(username, password);

        // 2. Generate Token
        try {
            String token = jwtHelper.generateToken(userDto.getUsername(), userDto.getRoles());
//            userDto.setToken(token); // Assuming UserDto has a 'token' field
            
            // 3. Sanitize sensitive data
            userDto.setPassword(null); 
            
            log.info("AuthService: Login successful. Token generated for user: {}", username);
            return userDto;
        } catch (Exception e) {
            log.error("AuthService: Error generating token for user: {}", username, e);
            throw new RuntimeException("Login failed during token generation.");
        }
    }

    @Override
    public UserDto logoutUser(String username) {
        log.info("AuthService: Logout request for user: {}", username);
        
        // In Stateless JWT, the server doesn't "delete" the session.
        // However, we can verify the user exists and perhaps blacklist the token if passed.
        
        try {
            UserDto userDto = userService.getUserByUsername(username);
            
            // Optional logic: Add current token to a Redis Blacklist here.
            
            log.info("AuthService: User logged out successfully (Client should discard token): {}", username);
            return userDto;
        } catch (in.food.restro.authservice.exception.ResourceNotFoundException e) {
            log.warn("AuthService: Logout attempted for non-existent user: {}", username);
            throw e;
        }
    }
}