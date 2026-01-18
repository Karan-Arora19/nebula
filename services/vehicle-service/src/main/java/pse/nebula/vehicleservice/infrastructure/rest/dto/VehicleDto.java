package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Vehicle;

import java.math.BigDecimal;

/**
 * DTO for vehicle data in Cars Overview response.
 */
public record VehicleDto(
        Integer vehicleId,
        String carName,
        CarType carType,
        Integer horsePower,
        BigDecimal basePrice,
        String image,
        String modelPath
) {
    /**
     * Creates a VehicleDto from a Vehicle entity.
     */
    public static VehicleDto fromEntity(Vehicle vehicle) {
        return new VehicleDto(
                vehicle.getId(),
                vehicle.getCarName(),
                vehicle.getCarType(),
                vehicle.getHorsePower(),
                vehicle.getBasePrice(),
                vehicle.getImage(),
                vehicle.getModelPath()
        );
    }
}

