package pse.nebula.vehicleservice.domain.port;

import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Paint;
import java.util.List;

/**
 * Repository interface for Paint entity.
 * Defines the contract for paint data access.
 */
public interface PaintRepository {

    /**
     * Find all paints with their prices eagerly loaded for a specific car type.
     *
     * @param carType the car type to filter prices
     * @return list of paints with prices for the given car type
     */
    List<Paint> findAllWithPricesForCarType(CarType carType);

    /**
     * Find all paints.
     *
     * @return list of all paints
     */
    List<Paint> findAll();

    /**
     * Save a paint.
     *
     * @param paint the paint to save
     * @return the saved paint
     */
    Paint save(Paint paint);
}

