package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest;

import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;

import java.util.List;
import java.util.Optional;

/**
 * Port interface for fetching vehicle data from external vehicle-service.
 * Following hexagonal architecture - this is the outbound port.
 */
public interface VehicleServiceClient {

    /**
     * Fetches all available vehicles from vehicle-service.
     *
     * @return list of available vehicles
     */
    List<VehicleDto> getAllVehicles();

    /**
     * Fetches a specific vehicle by ID from vehicle-service.
     *
     * @param vehicleId the ID of the vehicle to fetch
     * @return Optional containing the vehicle if found, empty otherwise
     */
    Optional<VehicleDto> getVehicleById(Integer vehicleId);
}

