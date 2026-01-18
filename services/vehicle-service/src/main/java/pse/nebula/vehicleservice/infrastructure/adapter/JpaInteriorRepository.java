package pse.nebula.vehicleservice.infrastructure.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Interior;
import pse.nebula.vehicleservice.domain.port.InteriorRepository;

import java.util.List;

/**
 * JPA implementation of InteriorRepository.
 */
@Repository
public interface JpaInteriorRepository extends JpaRepository<Interior, Integer>, InteriorRepository {

    /**
     * Find all interiors that have a price for the given car type.
     * Eagerly fetches only the prices for the specified car type to avoid loading unnecessary data.
     */
    @Override
    @Query("SELECT DISTINCT i FROM Interior i " +
           "LEFT JOIN FETCH i.prices ip " +
           "WHERE ip.carType = :carType")
    List<Interior> findAllWithPricesForCarType(@Param("carType") CarType carType);
}

