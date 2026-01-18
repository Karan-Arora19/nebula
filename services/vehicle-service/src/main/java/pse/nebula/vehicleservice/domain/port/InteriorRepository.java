package pse.nebula.vehicleservice.domain.port;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Interior;
import java.util.List;

/**
 * Repository interface for Interior entity.
 * Defines the contract for interior data access.
 */
public interface InteriorRepository {

    /**
     * Find all interiors with their prices eagerly loaded for a specific car type.
     *
     * @param carType the car type to filter prices
     * @return list of interiors with prices for the given car type
     */
    List<Interior> findAllWithPricesForCarType(CarType carType);

    /**
     * Find all interiors.
     *
     * @return list of all interiors
     */
    List<Interior> findAll();

    /**
     * Save an interior.
     *
     * @param interior the interior to save
     * @return the saved interior
     */
    Interior save(Interior interior);
}

