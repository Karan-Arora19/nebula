package pse.nebula.vehicleservice.domain.exception;

/**
 * Exception thrown when a requested vehicle is not found.
 */
public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(Integer vehicleId) {
        super("Vehicle not found with id: " + vehicleId);
    }
}

