package in.food.restro.authservice.exception;

import in.food.restro.authservice.dtos.ErrorResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ========================================================================
    // NEW HANDLERS FOR YOUR CUSTOM CLASSES
    // ========================================================================

    /**
     * Handles UserNotFoundException (HTTP 404).
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("User not found at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, path);
    }

    /**
     * Handles RoleNotFoundException (HTTP 404).
     */
    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFoundException(RoleNotFoundException ex, WebRequest request) {
        String path = extractPath(request);
        log.error("Role configuration error at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, path);
    }

    /**
     * Handles DuplicateResourceException (HTTP 409).
     * Useful for generic duplicates (e.g., Email already taken, Phone already taken).
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResourceException(DuplicateResourceException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Duplicate resource detected at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, path);
    }

    /**
     * Handles YOUR CUSTOM BadCredentialsException (HTTP 401).
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleCustomBadCredentials(BadCredentialsException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Login failed (Custom Exception) at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, path);
    }

    /**
     * Handles SPRING SECURITY'S BadCredentialsException (HTTP 401).
     * We use the fully qualified name here to avoid conflict with your custom class.
     */
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleSpringBadCredentials(org.springframework.security.authentication.BadCredentialsException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Login failed (Spring Security) at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse("Invalid username or password", HttpStatus.UNAUTHORIZED, path);
    }

    // ========================================================================
    // EXISTING HANDLERS
    // ========================================================================

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Registration failed at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.CONFLICT, path);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Resource lookup failed at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.NOT_FOUND, path);
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationFailedException(AuthenticationFailedException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Authentication failed at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, path);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Access denied at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse("You do not have permission to access this resource", HttpStatus.FORBIDDEN, path);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        String path = extractPath(request);
        String validationErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        
        log.warn("Validation failed at path [{}]: {}", path, validationErrors);
        return createErrorResponse("Validation failed: " + validationErrors, HttpStatus.BAD_REQUEST, path);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Data integrity violation at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse("Operation failed due to data conflict", HttpStatus.CONFLICT, path);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Invalid HTTP method at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex.getMessage(), HttpStatus.METHOD_NOT_ALLOWED, path);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        String path = extractPath(request);
        log.error("Unexpected system error at path [{}]: {}", path, ex.getMessage(), ex);
        return createErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR, path);
    }

    // -------------------------------------------------------------------------
    // Private Helper Methods
    // -------------------------------------------------------------------------

    private ResponseEntity<ErrorResponse> createErrorResponse(String message, HttpStatus status, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                Optional.ofNullable(message).orElse("Error occurred"),
                status.value(),
                status.getReasonPhrase(),
                LocalDateTime.now(),
                path
        );
        return new ResponseEntity<>(errorResponse, status);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}