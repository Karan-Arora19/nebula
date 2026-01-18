package pse.nebula.vehicleservice.infrastructure.rest.dto;

import java.time.Instant;

/**
 * DTO for error responses.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        String path,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(status, error, message, path, Instant.now());
    }
}

