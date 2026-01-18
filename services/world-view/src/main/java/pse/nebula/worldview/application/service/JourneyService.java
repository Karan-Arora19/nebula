package pse.nebula.worldview.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pse.nebula.worldview.domain.exception.JourneyAlreadyExistsException;
import pse.nebula.worldview.domain.exception.JourneyNotFoundException;
import pse.nebula.worldview.domain.model.Coordinate;
import pse.nebula.worldview.domain.model.DrivingRoute;
import pse.nebula.worldview.domain.model.JourneyState;
import pse.nebula.worldview.domain.port.inbound.JourneyUseCase;
import pse.nebula.worldview.domain.port.inbound.RouteUseCase;
import pse.nebula.worldview.domain.port.outbound.CoordinatePublisher;
import pse.nebula.worldview.domain.port.outbound.JourneyStateRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application service that implements journey-related use cases.
 * Orchestrates the journey lifecycle and coordinates between domain and infrastructure.
 *
 * Journeys are automatically managed - no manual control is exposed.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JourneyService implements JourneyUseCase {

    private final RouteUseCase routeUseCase;
    private final JourneyStateRepository journeyStateRepository;
    private final CoordinatePublisher coordinatePublisher;
    
    // Track last logged milestone per journey to avoid duplicate logs
    private final Map<String, Double> lastLoggedMilestone = new ConcurrentHashMap<>();
    
    // Milestone thresholds (0%, 25%, 50%, 75%, 90%, 100%)
    private static final double[] MILESTONES = {0.0, 25.0, 50.0, 75.0, 90.0, 100.0};

    @Override
    public JourneyState startNewJourney(String journeyId, double speedMetersPerSecond) {
        // Get a random route
        DrivingRoute route = routeUseCase.getRandomRoute();

        // Check if journey already exists
        if (journeyStateRepository.exists(journeyId)) {
            throw new JourneyAlreadyExistsException(journeyId);
        }

        // Create new journey state
        JourneyState journeyState = new JourneyState(journeyId, route, speedMetersPerSecond);
        journeyState.start();

        // Persist the journey state
        journeyStateRepository.save(journeyState);

        // Publish journey started event
        coordinatePublisher.publishJourneyStarted(journeyState);

        // Log journey start with correlation ID and key details
        log.info("[Journey: {}] Started - Route: \"{}\" ({} waypoints, {} m/s)",
                journeyId, route.name(), route.getTotalWaypoints(), speedMetersPerSecond);
        
        return journeyState;
    }

    @Override
    public JourneyState getJourneyState(String journeyId) {
        return journeyStateRepository.findById(journeyId)
            .orElseThrow(() -> new JourneyNotFoundException(journeyId));
    }

    @Override
    public Coordinate advanceJourney(String journeyId, double elapsedSeconds) {
        JourneyState journeyState = getJourneyState(journeyId);

        int totalWaypoints = journeyState.getRoute().getTotalWaypoints();
        double totalDistance = journeyState.getRoute().totalDistanceMeters();

        boolean completed = journeyState.advance(elapsedSeconds);

        // Save updated state
        journeyStateRepository.save(journeyState);

        // Publish coordinate update (always publish to MQTT for real-time updates)
        Coordinate currentPosition = journeyState.getCurrentPosition();
        int currentWaypoint = journeyState.getCurrentWaypointIndex();
        double progress = journeyState.getProgressPercentage();
        
        // Check if we've crossed a milestone threshold (0%, 25%, 50%, 75%, 90%, 100%)
        Double lastMilestone = lastLoggedMilestone.get(journeyId);
        double crossedMilestone = -1;
        
        for (double milestone : MILESTONES) {
            // Check if we've crossed this milestone (current >= milestone and last logged < milestone)
            if (progress >= milestone && (lastMilestone == null || lastMilestone < milestone)) {
                crossedMilestone = milestone;
                lastLoggedMilestone.put(journeyId, milestone);
                break;
            }
        }
        
        // Only log when we cross a milestone
        if (crossedMilestone >= 0) {
            log.info("[Journey: {}] Progress: {}% ({}/{} waypoints) - Position: [{}, {}]",
                    journeyId,
                    String.format("%.1f", progress),
                    currentWaypoint + 1,
                    totalWaypoints,
                    String.format("%.6f", currentPosition.latitude()),
                    String.format("%.6f", currentPosition.longitude()));
        }
        // No logging for non-milestone progress - MQTT handles real-time updates
        
        coordinatePublisher.publishCoordinateUpdate(journeyId, currentPosition, journeyState);

        if (completed) {
            // Calculate completion summary
            double distanceKm = totalDistance / 1000.0;
            double avgSpeedMps = journeyState.getSpeedMetersPerSecond();
            double avgSpeedKmh = avgSpeedMps * 3.6;
            
            // Log completion with summary metrics
            log.info("[Journey: {}] Completed - Distance: {}km, Avg Speed: {} m/s ({} km/h)",
                    journeyId,
                    String.format("%.2f", distanceKm),
                    String.format("%.2f", avgSpeedMps),
                    String.format("%.1f", avgSpeedKmh));
            
            coordinatePublisher.publishJourneyCompleted(journeyState);
        }

        return currentPosition;
    }


    @Override
    public void stopJourney(String journeyId) {
        log.debug("[Journey: {}] Stopping and cleaning up", journeyId);
        // Clean up milestone tracking
        lastLoggedMilestone.remove(journeyId);
        journeyStateRepository.delete(journeyId);
    }

    @Override
    public boolean journeyExists(String journeyId) {
        return journeyStateRepository.exists(journeyId);
    }
}