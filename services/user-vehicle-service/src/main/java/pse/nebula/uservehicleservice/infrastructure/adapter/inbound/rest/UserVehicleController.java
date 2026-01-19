package pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pse.nebula.uservehicleservice.application.service.UserVehicleAssignmentService;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto.ErrorResponse;
import pse.nebula.uservehicleservice.infrastructure.adapter.inbound.rest.dto.UserVehicleInfoResponse;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.mqtt.VehicleTelemetryPublisher;

/**
 * REST controller for user vehicle operations.
 * Provides endpoint for retrieving user's vehicle information and triggering MQTT publishing.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/user-vehicle")
@Tag(name = "User Vehicle", description = "User vehicle information and telemetry operations")
public class UserVehicleController {

    private static final String USER_ID_HEADER = "X-User-Id";

    private final UserVehicleAssignmentService assignmentService;
    private final VehicleTelemetryPublisher telemetryPublisher;

    public UserVehicleController(UserVehicleAssignmentService assignmentService,
                                  VehicleTelemetryPublisher telemetryPublisher) {
        this.assignmentService = assignmentService;
        this.telemetryPublisher = telemetryPublisher;
    }

    /**
     * Gets user's vehicle information including maintenance date and tyre pressures.
     * On first call, assigns a random vehicle to the user and starts MQTT telemetry publishing.
     *
     * @param userId the user ID from request header (validated by gateway)
     * @return user's vehicle maintenance and tyre pressure information
     */
    @GetMapping("/info")
    @Operation(
            summary = "Get user vehicle info",
            description = "Returns maintenance date and tyre pressures for user's vehicle. " +
                    "Assigns a random vehicle on first call and starts MQTT telemetry publishing."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved vehicle info",
                    content = @Content(schema = @Schema(implementation = UserVehicleInfoResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Missing or invalid user ID header",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Vehicle service unavailable",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<UserVehicleInfoResponse> getUserVehicleInfo(
            @Parameter(description = "User ID", required = true)
            @RequestHeader(USER_ID_HEADER) String userId) {

        log.info("Received request for user vehicle info. UserId: {}", userId);

        // Get or assign vehicle for the user
        UserVehicle userVehicle = assignmentService.getOrAssignVehicle(userId);

        // Start MQTT telemetry publishing (idempotent - won't duplicate if already publishing)
        telemetryPublisher.startPublishing(userId, userVehicle.getVehicleName());

        // Build and return response
        UserVehicleInfoResponse response = UserVehicleInfoResponse.fromEntity(userVehicle);

        log.info("Returning vehicle info for user: {}. MaintenanceDue: {}, Vehicle: {}",
                userId, response.maintenanceDueDate(), userVehicle.getVehicleName());

        return ResponseEntity.ok(response);
    }
}

