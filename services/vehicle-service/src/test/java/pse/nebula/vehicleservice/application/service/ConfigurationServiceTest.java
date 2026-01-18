package pse.nebula.vehicleservice.application.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pse.nebula.vehicleservice.application.service.ConfigurationService.VehicleConfiguration;
import pse.nebula.vehicleservice.domain.exception.VehicleNotFoundException;
import pse.nebula.vehicleservice.domain.model.*;
import pse.nebula.vehicleservice.domain.port.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigurationService Tests")
class ConfigurationServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private PaintRepository paintRepository;

    @Mock
    private RimRepository rimRepository;

    @Mock
    private InteriorRepository interiorRepository;

    @InjectMocks
    private ConfigurationService configurationService;

    private Vehicle sportsVehicle;
    private Paint blackPaint;
    private Rim baseRim;
    private Interior blackInterior;

    @BeforeEach
    void setUp() {
        sportsVehicle = new Vehicle("Furari", CarType.SPORTS, 670, new BigDecimal("245000.00"), "furarri-hero", "/models/furarri.glb");

        blackPaint = new Paint("Black", "Timeless deep black metallic finish", "black", "#000000");
        blackPaint.addPrice(new PaintPrice(CarType.SPORTS, new BigDecimal("500.00")));

        baseRim = new Rim("Sport 19\"", "Standard 19-inch sport alloy wheels", "rim-19-base", "sport");
        baseRim.addPrice(new RimPrice(CarType.SPORTS, BigDecimal.ZERO));

        blackInterior = new Interior("Black Leather", "Classic black leather upholstery", "interior-black-leather", "black");
        blackInterior.addPrice(new InteriorPrice(CarType.SPORTS, BigDecimal.ZERO));
    }

    @Nested
    @DisplayName("getConfigurationForVehicle")
    class GetConfigurationForVehicleTests {

        @Test
        @DisplayName("should return configuration with resolved prices for vehicle's car type")
        void shouldReturnConfigurationWithResolvedPrices() {
            // Given
            Integer vehicleId = 1;
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(sportsVehicle));
            when(paintRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackPaint));
            when(rimRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(baseRim));
            when(interiorRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackInterior));

            // When
            VehicleConfiguration result = configurationService.getConfigurationForVehicle(vehicleId);

            // Then
            assertThat(result.vehicle()).isEqualTo(sportsVehicle);
            assertThat(result.paints()).hasSize(1);
            assertThat(result.rims()).hasSize(1);
            assertThat(result.interiors()).hasSize(1);
        }

        @Test
        @DisplayName("should return correct paint price for car type")
        void shouldReturnCorrectPaintPrice() {
            // Given
            Integer vehicleId = 1;
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(sportsVehicle));
            when(paintRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackPaint));
            when(rimRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(baseRim));
            when(interiorRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackInterior));

            // When
            VehicleConfiguration result = configurationService.getConfigurationForVehicle(vehicleId);

            // Then
            BigDecimal paintPrice = result.getPaintPrice(blackPaint);
            assertThat(paintPrice).isEqualByComparingTo(new BigDecimal("500.00"));
        }

        @Test
        @DisplayName("should return zero for included options")
        void shouldReturnZeroForIncludedOptions() {
            // Given
            Integer vehicleId = 1;
            when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(sportsVehicle));
            when(paintRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackPaint));
            when(rimRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(baseRim));
            when(interiorRepository.findAllWithPricesForCarType(CarType.SPORTS)).thenReturn(List.of(blackInterior));

            // When
            VehicleConfiguration result = configurationService.getConfigurationForVehicle(vehicleId);

            // Then
            BigDecimal rimPrice = result.getRimPrice(baseRim);
            BigDecimal interiorPrice = result.getInteriorPrice(blackInterior);
            assertThat(rimPrice).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(interiorPrice).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("should throw VehicleNotFoundException when vehicle not found")
        void shouldThrowExceptionWhenVehicleNotFound() {
            // Given
            when(vehicleRepository.findById(999)).thenReturn(Optional.empty());

            // When/Then
            assertThatThrownBy(() -> configurationService.getConfigurationForVehicle(999))
                    .isInstanceOf(VehicleNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("VehicleConfiguration record")
    class VehicleConfigurationTests {

        @Test
        @DisplayName("should return zero when paint has no prices")
        void shouldReturnZeroWhenPaintHasNoPrices() {
            // Given
            Paint emptyPaint = new Paint("Empty", "No prices", "empty", "#FFFFFF");
            VehicleConfiguration config = new VehicleConfiguration(
                    sportsVehicle, List.of(emptyPaint), List.of(), List.of(), CarType.SPORTS);

            // When
            BigDecimal price = config.getPaintPrice(emptyPaint);

            // Then
            assertThat(price).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }
}

