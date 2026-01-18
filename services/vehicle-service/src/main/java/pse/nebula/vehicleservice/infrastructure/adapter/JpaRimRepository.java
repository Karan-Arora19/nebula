package pse.nebula.vehicleservice.infrastructure.adapter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pse.nebula.vehicleservice.domain.model.CarType;
import pse.nebula.vehicleservice.domain.model.Rim;
import pse.nebula.vehicleservice.domain.port.RimRepository;

import java.util.List;

/**
 * JPA implementation of RimRepository.
 */
@Repository
public interface JpaRimRepository extends JpaRepository<Rim, Integer>, RimRepository {

    /**
     * Find all rims that have a price for the given car type.
     * Eagerly fetches only the prices for the specified car type to avoid loading unnecessary data.
     */
    @Override
    @Query("SELECT DISTINCT r FROM Rim r " +
           "LEFT JOIN FETCH r.prices rp " +
           "WHERE rp.carType = :carType")
    List<Rim> findAllWithPricesForCarType(@Param("carType") CarType carType);
}

