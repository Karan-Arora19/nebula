package pse.nebula.vehicleservice.application.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import pse.nebula.vehicleservice.domain.port.VehicleRepository;

import java.util.List;

/**
 * Service for vehicle-related operations.
 * Handles the Cars Overview functionality.
 */
@Service
@Transactional(readOnly = true)
public class VehicleService {

    public static final String CACHE_VEHICLES = "vehicles";
    public static final String CACHE_VEHICLE_BY_ID = "vehicleById";

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Get all vehicles for the Cars Overview page.
     *
     * @return list of all vehicles
     */
    @Cacheable(value = CACHE_VEHICLES)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Get all vehicles with pagination for the Cars Overview page.
     *
     * @param pageable pagination information
     * @return page of vehicles
     */
    @Cacheable(value = CACHE_VEHICLES, key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<Vehicle> getAllVehicles(Pageable pageable) {
        return vehicleRepository.findAll(pageable);
    }

    /**
     * Get a specific vehicle by ID.
     *
     * @param vehicleId the vehicle ID
     * @return the vehicle
     * @throws VehicleNotFoundException if vehicle not found
     */
    @Cacheable(value = CACHE_VEHICLE_BY_ID, key = "#vehicleId")
    public Vehicle getVehicleById(Integer vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }
}

