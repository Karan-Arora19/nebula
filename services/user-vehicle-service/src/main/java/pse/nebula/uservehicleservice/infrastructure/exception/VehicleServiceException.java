package pse.nebula.uservehicleservice.infrastructure.exception;

/**
 * Exception thrown when communication with vehicle-service fails.
 */
public class VehicleServiceException extends RuntimeException {

    public VehicleServiceException(String message) {
        super(message);
    }

    public VehicleServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}

