package in.food.restro.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT) // Returns 409 Conflict to the client
public class UserAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for a simple message.
     * Example: new UserAlreadyExistsException("User with username 'abc' already exists");
     */
    public UserAlreadyExistsException(String message) {
        super(message);
    }

    /**
     * Constructor for a message and a cause (for chaining exceptions).
     */
    public UserAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}