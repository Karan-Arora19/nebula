package pse.nebula.uservehicleservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pse.nebula.uservehicleservice.domain.model.UserVehicle;

import java.util.Optional;

/**
 * Repository for UserVehicle entity persistence operations.
 */
@Repository
public interface UserVehicleRepository extends JpaRepository<UserVehicle, Long> {

    /**
     * Finds a user-vehicle assignment by user ID.
     *
     * @param userId the unique identifier of the user
     * @return an Optional containing the UserVehicle if found, empty otherwise
     */
    Optional<UserVehicle> findByUserId(String userId);

    /**
     * Checks if a user already has a vehicle assigned.
     *
     * @param userId the unique identifier of the user
     * @return true if user has a vehicle assigned, false otherwise
     */
    boolean existsByUserId(String userId);
}

