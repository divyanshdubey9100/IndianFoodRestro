package in.food.restro.authservice.exception;

import in.food.restro.authservice.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles Custom UserNotFoundException (HTTP 404).
     * This is much cleaner than checking string contains("not found").
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("User lookup failed at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex, HttpStatus.NOT_FOUND, path);
    }

    /**
     * Handles IllegalArgumentException (HTTP 400).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest request) {
        String path = extractPath(request);
        log.warn("Invalid argument at path [{}]: {}", path, ex.getMessage());
        return createErrorResponse(ex, HttpStatus.BAD_REQUEST, path);
    }

    /**
     * Handles generic RuntimeException (HTTP 500).
     * Now strictly for unexpected runtime issues.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        String path = extractPath(request);
        log.error("Runtime exception at path [{}]: {}", path, ex.getMessage(), ex);
        return createErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, path);
    }

    /**
     * Fallback for all other Exceptions (HTTP 500).
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex, WebRequest request) {
        String path = extractPath(request);
        log.error("Unexpected system error at path [{}]: {}", path, ex.getMessage(), ex);

        String customMessage = "An unexpected error occurred: " + 
                               Optional.ofNullable(ex.getMessage()).orElse("No details available");

        ErrorResponse errorResponse = new ErrorResponse(
                customMessage,
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                LocalDateTime.now(),
                path
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // -------------------------------------------------------------------------
    // Private Helper Methods
    // -------------------------------------------------------------------------

    private ResponseEntity<ErrorResponse> createErrorResponse(Exception ex, HttpStatus status, String path) {
        ErrorResponse errorResponse = new ErrorResponse(
                Optional.ofNullable(ex.getMessage()).orElse("Error occurred"),
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