package pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto;

import java.util.List;

/**
 * DTO representing the paginated response from vehicle-service.
 * Maps to VehiclesOverviewResponse from vehicle-service API.
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
}

