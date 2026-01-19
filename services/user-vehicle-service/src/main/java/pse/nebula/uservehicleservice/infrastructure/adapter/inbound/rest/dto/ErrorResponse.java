package pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto;

import java.time.Instant;

/**
 * Standard error response DTO.
 */
public record ErrorResponse(
        int status,
        String error,
        String message,
        Instant timestamp
) {
    public static ErrorResponse of(int status, String error, String message) {
        return new ErrorResponse(status, error, message, Instant.now());
    }
}

