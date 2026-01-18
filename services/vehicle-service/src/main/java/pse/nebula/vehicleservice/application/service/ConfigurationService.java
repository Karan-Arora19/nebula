package pse.nebula.vehicleservice.application.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.*;
import pse.nebula.vehicleservice.domain.port.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service for car configuration operations.
 * Handles fetching configuration options with prices resolved by car type.
 */
@Service
@Transactional(readOnly = true)
public class ConfigurationService {

    public static final String CACHE_VEHICLE_CONFIGURATION = "vehicleConfiguration";

    private final VehicleRepository vehicleRepository;
    private final PaintRepository paintRepository;
    private final RimRepository rimRepository;
    private final InteriorRepository interiorRepository;

    public ConfigurationService(
            VehicleRepository vehicleRepository,
            PaintRepository paintRepository,
            RimRepository rimRepository,
            InteriorRepository interiorRepository) {
        this.vehicleRepository = vehicleRepository;
        this.paintRepository = paintRepository;
        this.rimRepository = rimRepository;
        this.interiorRepository = interiorRepository;
    }

    /**
     * Get all configuration options for a specific vehicle.
     * Prices are resolved based on the vehicle's car type.
     *
     * @param vehicleId the vehicle ID
     * @return configuration options with resolved prices
     * @throws VehicleNotFoundException if vehicle not found
     */
    @Cacheable(value = CACHE_VEHICLE_CONFIGURATION, key = "#vehicleId")
    public VehicleConfiguration getConfigurationForVehicle(Integer vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));

        CarType carType = vehicle.getCarType();

        // Fetch all options with prices for the specific car type
        List<Paint> paints = paintRepository.findAllWithPricesForCarType(carType);
        List<Rim> rims = rimRepository.findAllWithPricesForCarType(carType);
        List<Interior> interiors = interiorRepository.findAllWithPricesForCarType(carType);

        return new VehicleConfiguration(vehicle, paints, rims, interiors, carType);
    }

    /**
     * Holds the configuration options for a vehicle.
     * Includes all paints, rims, and interiors with prices resolved for the vehicle's car type.
     */
    public record VehicleConfiguration(
            Vehicle vehicle,
            List<Paint> paints,
            List<Rim> rims,
            List<Interior> interiors,
            CarType carType
    ) {
        /**
         * Get the price for a specific paint option for this vehicle's car type.
         */
        public BigDecimal getPaintPrice(Paint paint) {
            return paint.getPrices().stream()
                    .filter(p -> p.getCarType() == carType)
                    .findFirst()
                    .map(PaintPrice::getPrice)
                    .orElse(BigDecimal.ZERO);
        }

        /**
         * Get the price for a specific rim option for this vehicle's car type.
         */
        public BigDecimal getRimPrice(Rim rim) {
            return rim.getPrices().stream()
                    .filter(p -> p.getCarType() == carType)
                    .findFirst()
                    .map(RimPrice::getPrice)
                    .orElse(BigDecimal.ZERO);
        }

        /**
         * Get the price for a specific interior option for this vehicle's car type.
         */
        public BigDecimal getInteriorPrice(Interior interior) {
            return interior.getPrices().stream()
                    .filter(p -> p.getCarType() == carType)
                    .findFirst()
                    .map(InteriorPrice::getPrice)
                    .orElse(BigDecimal.ZERO);
        }
    }
}

