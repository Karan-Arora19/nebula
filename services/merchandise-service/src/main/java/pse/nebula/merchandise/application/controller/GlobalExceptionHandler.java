package pse.nebula.merchandise.application.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Map;

/**
 * Global exception handler to ensure proper HTTP status codes are returned.
 * This fixes the issue where ResponseStatusException (e.g., 404) was being
 * converted to 403 by Spring Security's exception handling.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatusException(ResponseStatusException ex) {
        Map<String, Object> body = Map.of(
            "timestamp", Instant.now().toString(),
            "status", ex.getStatusCode().value(),
            "error", ex.getStatusCode().toString(),
            "message", ex.getReason() != null ? ex.getReason() : "No message available"
        );
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
}
