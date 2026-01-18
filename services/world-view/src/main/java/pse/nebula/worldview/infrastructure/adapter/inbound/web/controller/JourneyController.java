package pse.nebula.worldview.infrastructure.adapter.inbound.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pse.nebula.worldview.application.service.AutoJourneySchedulerService;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.dto.JourneyStateDto;
import pse.nebula.worldview.infrastructure.adapter.inbound.web.mapper.DtoMapper;

import java.util.Optional;

/**
 * REST controller for journey-related operations.
 * Provides read-only endpoints to query the current journey state.
 *
 * Journeys are automatically managed by the AutoJourneySchedulerService.
 * Users cannot start, stop, or pause journeys - they run automatically.
 *
 * Real-time coordinate streaming is handled via MQTT (RabbitMQ).
 * Frontend subscribes to MQTT topics: nebula/journey/{journeyId}/position
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/journeys")
@RequiredArgsConstructor
@Tag(name = "Journeys", description = "Endpoints for viewing journey status (read-only)")
public class JourneyController {

    private final JourneyUseCase journeyUseCase;
    private final AutoJourneySchedulerService autoJourneySchedulerService;
    private final DtoMapper dtoMapper;

    @Operation(summary = "Get current active journey",
            description = "Returns the current automatically running journey state, if any. " +
                    "Journeys are automatically started by the system on random routes.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Active journey found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "204", description = "No active journey currently running")
    })
    @GetMapping("/current")
    public ResponseEntity<JourneyStateDto> getCurrentJourney() {
        // Silent polling - no logging for routine frontend polling requests
        Optional<JourneyState> activeJourney = autoJourneySchedulerService.getActiveJourneyState();

        if (activeJourney.isPresent()) {
            return ResponseEntity.ok(dtoMapper.toDto(activeJourney.get()));
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @Operation(summary = "Check if journey is active",
            description = "Returns whether there is currently an active journey running")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = Boolean.class)))
    })
    @GetMapping("/active")
    public ResponseEntity<Boolean> isJourneyActive() {
        boolean isActive = autoJourneySchedulerService.hasActiveJourney();
        return ResponseEntity.ok(isActive);
    }

    @Operation(summary = "Get journey state by ID",
            description = "Returns the state of a specific journey by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Journey state retrieved",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = JourneyStateDto.class))),
            @ApiResponse(responseCode = "404", description = "Journey not found", content = @Content)
    })
    @GetMapping("/{journeyId}")
    public ResponseEntity<JourneyStateDto> getJourneyState(
            @Parameter(description = "Unique journey identifier", example = "auto-journey-abc12345")
            @PathVariable String journeyId) {
        // Log only at TRACE level - routine lookups don't need logging
        log.trace("Fetching state for journey: {}", journeyId);

        JourneyState journeyState = journeyUseCase.getJourneyState(journeyId);
        return ResponseEntity.ok(dtoMapper.toDto(journeyState));
    }
}