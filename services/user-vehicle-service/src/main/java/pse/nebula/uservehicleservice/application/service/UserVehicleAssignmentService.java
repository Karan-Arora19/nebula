package pse.nebula.uservehicleservice.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;
import pse.nebula.uservehicleservice.domain.repository.UserVehicleRepository;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.VehicleServiceClient;
import pse.nebula.uservehicleservice.infrastructure.adapter.outbound.rest.dto.VehicleDto;
import pse.nebula.uservehicleservice.infrastructure.exception.VehicleServiceException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Service responsible for managing user-vehicle assignments.
 * Handles vehicle assignment and maintenance date calculation.
 */
@Slf4j
@Service
public class UserVehicleAssignmentService {

    private static final int MAINTENANCE_MONTHS_AHEAD = 6;

    private final UserVehicleRepository userVehicleRepository;
    private final VehicleServiceClient vehicleServiceClient;
    private final Random random;

    public UserVehicleAssignmentService(UserVehicleRepository userVehicleRepository,
                                         VehicleServiceClient vehicleServiceClient) {
        this.userVehicleRepository = userVehicleRepository;
        this.vehicleServiceClient = vehicleServiceClient;
        this.random = new Random();
    }

    /**
     * Gets or creates a user-vehicle assignment.
     * If user doesn't have a vehicle assigned, assigns a random one from vehicle-service.
     *
     * @param userId the unique identifier of the user
     * @return the user's vehicle assignment
     */
    @Transactional
    public UserVehicle getOrAssignVehicle(String userId) {
        log.debug("Getting or assigning vehicle for user: {}", userId);

        Optional<UserVehicle> existingAssignment = userVehicleRepository.findByUserId(userId);

        if (existingAssignment.isPresent()) {
            log.info("Found existing vehicle assignment for user: {}", userId);
            return existingAssignment.get();
        }

        log.info("No existing assignment found. Assigning new vehicle for user: {}", userId);
        return assignRandomVehicle(userId);
    }

    /**
     * Assigns a random vehicle to the user.
     *
     * @param userId the unique identifier of the user
     * @return the newly created user-vehicle assignment
     */
    private UserVehicle assignRandomVehicle(String userId) {
        List<VehicleDto> vehicles = vehicleServiceClient.getAllVehicles();

        if (vehicles.isEmpty()) {
            log.error("No vehicles available in vehicle-service");
            throw new VehicleServiceException("No vehicles available for assignment");
        }

        VehicleDto selectedVehicle = vehicles.get(random.nextInt(vehicles.size()));
        log.info("Selected vehicle for user {}: {} (ID: {})", userId, selectedVehicle.carName(), selectedVehicle.vehicleId());

        UserVehicle userVehicle = new UserVehicle(
                userId,
                selectedVehicle.vehicleId(),
                selectedVehicle.carName(),
                calculateMaintenanceDueDate()
        );

        UserVehicle savedAssignment = userVehicleRepository.save(userVehicle);
        log.info("Successfully assigned vehicle {} to user {}. Maintenance due: {}",
                savedAssignment.getVehicleName(), userId, savedAssignment.getMaintenanceDueDate());

        return savedAssignment;
    }

    /**
     * Calculates the maintenance due date (6 months from now).
     *
     * @return the maintenance due date
     */
    private LocalDate calculateMaintenanceDueDate() {
        return LocalDate.now().plusMonths(MAINTENANCE_MONTHS_AHEAD);
    }
}

