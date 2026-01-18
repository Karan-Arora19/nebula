package pse.nebula.vehicleservice.infrastructure.rest.dto;

import org.springframework.data.domain.Page;
import pse.nebula.vehicleservice.domain.model.Vehicle;

import java.util.List;

/**
 * DTO for Cars Overview response containing vehicles with pagination info.
 */
public record VehiclesOverviewResponse(
        List<VehicleDto> vehicles,
        int currentPage,
        int totalPages,
        long totalElements,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {
    /**
     * Create response from a Page of vehicles.
     */
    public static VehiclesOverviewResponse fromPage(Page<Vehicle> page) {
        List<VehicleDto> vehicleDtos = page.getContent().stream()
                .map(VehicleDto::fromEntity)
                .toList();

        return new VehiclesOverviewResponse(
                vehicleDtos,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    /**
     * Create response from a list of vehicles (backward compatibility, no pagination).
     */
    public static VehiclesOverviewResponse of(List<VehicleDto> vehicles) {
        return new VehiclesOverviewResponse(
                vehicles,
                0,
                1,
                vehicles.size(),
                vehicles.size(),
                false,
                false
        );
    }
}

