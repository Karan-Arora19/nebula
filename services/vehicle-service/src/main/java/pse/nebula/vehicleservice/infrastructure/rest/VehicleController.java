package pse.nebula.vehicleservice.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pse.nebula.vehicleservice.application.service.ConfigurationService;
import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.application.service.VehicleService;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import pse.nebula.vehicleservice.infrastructure.rest.dto.*;

/**
 * REST controller for vehicle operations.
 * Provides endpoints for Cars Overview and Car Configurator.
 */
@RestController
@RequestMapping("/api/v1/vehicles")
@Validated
@Tag(name = "Vehicles", description = "Vehicle and configuration operations")
public class VehicleController {

    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    private final VehicleService vehicleService;
    private final ConfigurationService configurationService;

    public VehicleController(VehicleService vehicleService, ConfigurationService configurationService) {
        this.vehicleService = vehicleService;
        this.configurationService = configurationService;
    }

    /**
     * Get all vehicles for the Cars Overview page with pagination.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return paginated list of vehicles
     */
    @GetMapping
    @Operation(summary = "Get all vehicles", description = "Returns all available vehicles for the Cars Overview page with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicles",
                    content = @Content(schema = @Schema(implementation = VehiclesOverviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VehiclesOverviewResponse> getAllVehicles(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page must be >= 0") int page,
            @Parameter(description = "Page size (1-100)")
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "Size must be >= 1") @Max(value = 100, message = "Size must be <= 100") int size) {

        logger.info("Received request to get all vehicles - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehiclePage = vehicleService.getAllVehicles(pageable);
        
        VehiclesOverviewResponse response = VehiclesOverviewResponse.fromPage(vehiclePage);
        logger.info("Returning {} vehicles", response.vehicles().size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific vehicle by ID.
     *
     * @param id the vehicle ID
     * @return the vehicle details
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get vehicle by ID", description = "Returns a specific vehicle by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved vehicle",
                    content = @Content(schema = @Schema(implementation = VehicleDto.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<VehicleDto> getVehicleById(
            @Parameter(description = "Vehicle ID", required = true)
            @PathVariable @NotNull @Positive(message = "Vehicle ID must be a positive number") Integer id) {
        VehicleDto vehicle = VehicleDto.fromEntity(vehicleService.getVehicleById(id));
        return ResponseEntity.ok(vehicle);
    }

    /**
     * Get configuration options for a specific vehicle.
     * Returns all paints, rims, and interiors with prices resolved for the vehicle's car type.
     *
     * @param id the vehicle ID
     * @return configuration options with resolved prices
     */
    @GetMapping("/{id}/configuration")
    @Operation(summary = "Get configuration options",
               description = "Returns all configuration options (paints, rims, interiors) with prices resolved for the vehicle's car type")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved configuration",
                    content = @Content(schema = @Schema(implementation = ConfigurationResponse.class))),
            @ApiResponse(responseCode = "404", description = "Vehicle not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid vehicle ID",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ConfigurationResponse> getVehicleConfiguration(
            @Parameter(description = "Vehicle ID", required = true)
            @PathVariable @NotNull @Positive(message = "Vehicle ID must be a positive number") Integer id) {
        logger.info("Received request to get configuration for vehicle ID: {}", id);
        VehicleConfiguration configuration = configurationService.getConfigurationForVehicle(id);
        ConfigurationResponse response = ConfigurationResponse.fromVehicleConfiguration(configuration);
        logger.info("Returning configuration for vehicle: {} with {} categories", response.name(), response.categories().size());
        return ResponseEntity.ok(response);
    }
}

