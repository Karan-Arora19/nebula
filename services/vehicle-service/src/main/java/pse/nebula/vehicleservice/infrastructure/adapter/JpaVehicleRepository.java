package pse.nebula.vehicleservice.infrastructure.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pse.nebula.vehicleservice.domain.model.Vehicle;
import pse.nebula.vehicleservice.domain.port.VehicleRepository;

/**
 * JPA implementation of VehicleRepository.
 */
@Repository
public interface JpaVehicleRepository extends JpaRepository<Vehicle, Integer>, VehicleRepository {
    // JpaRepository already provides findAll(), findById(), and save()
    // No additional methods needed
}

