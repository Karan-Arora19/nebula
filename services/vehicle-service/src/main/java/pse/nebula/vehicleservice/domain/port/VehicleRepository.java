package pse.nebula.vehicleservice.domain.port;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Vehicle entity.
 * Defines the contract for vehicle data access.
 */
public interface VehicleRepository {

    /**
     * Find all vehicles.
     *
     * @return list of all vehicles
     */
    List<Vehicle> findAll();

    /**
     * Find all vehicles with pagination.
     *
     * @param pageable pagination information
     * @return page of vehicles
     */
    Page<Vehicle> findAll(Pageable pageable);

    /**
     * Find a vehicle by its ID.
     *
     * @param id the vehicle ID
     * @return optional containing the vehicle if found
     */
    Optional<Vehicle> findById(Integer id);

    /**
     * Save a vehicle.
     *
     * @param vehicle the vehicle to save
     * @return the saved vehicle
     */
    Vehicle save(Vehicle vehicle);
}

