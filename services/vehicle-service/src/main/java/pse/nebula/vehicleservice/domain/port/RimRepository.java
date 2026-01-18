package pse.nebula.vehicleservice.domain.port;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Rim;
import java.util.List;

/**
 * Repository interface for Rim entity.
 * Defines the contract for rim data access.
 */
public interface RimRepository {

    /**
     * Find all rims with their prices eagerly loaded for a specific car type.
     *
     * @param carType the car type to filter prices
     * @return list of rims with prices for the given car type
     */
    List<Rim> findAllWithPricesForCarType(CarType carType);

    /**
     * Find all rims.
     *
     * @return list of all rims
     */
    List<Rim> findAll();

    /**
     * Save a rim.
     *
     * @param rim the rim to save
     * @return the saved rim
     */
    Rim save(Rim rim);
}

