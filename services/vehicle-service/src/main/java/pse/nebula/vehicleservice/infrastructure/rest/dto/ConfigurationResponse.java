package pse.nebula.vehicleservice.infrastructure.rest.dto;

import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.domain.model.CarType;

import java.util.List;

/**
 * DTO for configuration options response.
 * Contains vehicle info and configuration categories with prices resolved for the vehicle's car type.
 * Matches the frontend expected structure.
 */
public record ConfigurationResponse(
        Integer id,
        String name,
        String modelPath,
        Integer basePrice,
        List<CategoryDto> categories
) {
    /**
     * Creates a ConfigurationResponse from a VehicleConfiguration.
     */
    public static ConfigurationResponse fromVehicleConfiguration(VehicleConfiguration config) {
        CarType carType = config.carType();

        List<PaintOptionDto> paintDtos = config.paints().stream()
                .map(paint -> PaintOptionDto.fromEntity(paint, carType))
                .toList();

        List<RimOptionDto> rimDtos = config.rims().stream()
                .map(rim -> RimOptionDto.fromEntity(rim, carType))
                .toList();

        List<InteriorOptionDto> interiorDtos = config.interiors().stream()
                .map(interior -> InteriorOptionDto.fromEntity(interior, carType))
                .toList();

        List<CategoryDto> categories = List.of(
                CategoryDto.createPaintCategory(paintDtos),
                CategoryDto.createRimsCategory(rimDtos),
                CategoryDto.createInteriorCategory(interiorDtos)
        );

        return new ConfigurationResponse(
                config.vehicle().getId(),
                config.vehicle().getCarName(),
                config.vehicle().getModelPath(),
                config.vehicle().getBasePrice().intValue(),
                categories
        );
    }
}

