package in.food.restro.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN) // Returns 403 Forbidden to the client
public class AccessDeniedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructor for a simple message.
     * Example: new AccessDeniedException("You do not have permission to perform this action");
     */
    public AccessDeniedException(String message) {
        super(message);
    }

    /**
     * Constructor for a message and a cause (for chaining exceptions).
     */
    public AccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}