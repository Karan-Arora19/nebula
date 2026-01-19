package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto;

import java.math.BigDecimal;

/**
 * DTO representing a vehicle from vehicle-service.
 * Maps to the VehicleDto from vehicle-service API response.
 */
public record VehicleDto(
        Integer vehicleId,
        String carName,
        String carType,
        Integer horsePower,
        BigDecimal basePrice,
        String image,
        String modelPath
) {
}

